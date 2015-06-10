package edu.umich.rosie.language;

import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import edu.umich.rosie.AgentConnector;
import edu.umich.rosie.SoarAgent;
import edu.umich.rosie.SoarUtil;
import sml.Identifier;
import sml.WMElement;


public class LanguageConnector extends AgentConnector {
	public enum MessageType{
		AGENT_MESSAGE, INSTRUCTOR_MESSAGE
	};
	
    private TextToSpeech tts;  
    private SpeechToText stt;
    
    private ChatPanel chat;
    
	private Messages messages;
	
	Identifier languageId = null;
	
	public LanguageConnector(SoarAgent agent, Properties props){
		super(agent);
		
		String speechFile = props.getProperty("speech-file", "audio_file/sample");
		
        this.tts = new TextToSpeech();
        this.stt = new SpeechToText(speechFile, agent);
       
        messages = new Messages();
    	
        this.setOutputHandlerNames(new String[]{ "send-message" });
	}
	
	public void setChat(ChatPanel chat){
		this.chat = chat;
	}
	
	public ChatPanel getChat(){
		return chat;
	}
	
	public TextToSpeech getTTS(){
		return tts;
	}
	
	public SpeechToText getSTT(){
		return stt;
	}
	
	public synchronized void registerNewMessage(String message, MessageType msgType){
		switch(msgType){
    	case INSTRUCTOR_MESSAGE:
    		messages.addMessage(message);
    		break;
    	case AGENT_MESSAGE:
    		tts.speak(message);
    		break;
    	}
   		chat.registerNewMessage(message, msgType);
    	sendLCMChatMessage(message, msgType);
	}
    
    private void sendLCMChatMessage(String message, MessageType msgType){
// AM: TODO
//    	// Print message for logging
//    	chat_message_t chat_message = new chat_message_t();
//    	chat_message.utime = TimeUtil.utime();
//    	chat_message.message = message;
//    	chat_message.sender = sender;
//    	LCM.getSingleton().publish("CHAT_MESSAGES", chat_message);
    }
    
    public void clear(){
    	messages.destroy();
    }
    
    public void destroyMessage(int id){
    	messages.destroy();
    }
    
    public void newMessage(String message){
    	messages.addMessage(message);
    }
    
    @Override
    protected void onInputPhase(Identifier inputLink)
    {
   		messages.updateInputLink(inputLink);
    }

    @Override
    protected void onOutputEvent(String attName, Identifier id){
    	if (attName.equals("send-message")){
    		processOutputLinkMessage(id);
    	}
    }

	private void processOutputLinkMessage(Identifier messageId)
    {	
        if (messageId.GetNumberChildren() == 0)
        {
            messageId.CreateStringWME("status", "error");
            throw new IllegalStateException("Message has no children");
        }
        
        if(SoarUtil.getIdentifierOfAttribute(messageId, "first") == null){
        	processAgentMessageStructureCommand(messageId);
        } else {
        	processAgentMessageStringCommand(messageId);
        }
    }
	
    private void processAgentMessageStructureCommand(Identifier messageId)
    {
        String type = SoarUtil.getValueOfAttribute(messageId, "type",
                "Message does not have ^type");
        String message = "";
        message = AgentMessageParser.translateAgentMessage(messageId);
        if(message != null && !message.equals("")){
        	this.registerNewMessage(message, MessageType.AGENT_MESSAGE);
        }
        messageId.CreateStringWME("status", "complete");
    }
	
	private void processAgentMessageStringCommand(Identifier messageId){

        String message = "";
        WMElement wordsWME = messageId.FindByAttribute("first", 0);
        if (wordsWME == null || !wordsWME.IsIdentifier())
        {
            messageId.CreateStringWME("status", "error");
            throw new IllegalStateException("Message has no first attribute");
        }
        Identifier currentWordId = wordsWME.ConvertToIdentifier();

        // Follows the linked list down until it can't find the 'rest' attribute
        // of a WME
        while (currentWordId != null)
        {
            Identifier nextWordId = null;
            for (int i = 0; i < currentWordId.GetNumberChildren(); i++)
            {
                WMElement child = currentWordId.GetChild(i);
                if (child.GetAttribute().equals("value"))
                {
                    message += child.GetValueAsString()+ " ";
                }
                else if (child.GetAttribute().equals("next")
                        && child.IsIdentifier())
                {
                    nextWordId = child.ConvertToIdentifier();
                }
            }
            currentWordId = nextWordId;
        }

        if (message == "")
        {
            messageId.CreateStringWME("status", "error");
            throw new IllegalStateException("Message was empty");
        }

        message += ".";
        this.registerNewMessage(message.substring(0, message.length() - 1), MessageType.AGENT_MESSAGE);

        messageId.CreateStringWME("status", "complete");
    }

	@Override
	protected void onInitSoar() { }

	@Override
	public void createMenu(JMenuBar menuBar) {}
}