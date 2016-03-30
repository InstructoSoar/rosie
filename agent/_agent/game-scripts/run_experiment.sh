suffix=".preparse"
stats=".stats"
maxstats=".maxstats"
smem=".smem"
topstate=".top"
csv=".csv"
dialog=".dialog"
pertaskstats=".pertaskstats"

dc=".dc"

rm pertaskstats.txt
rm smemuse.txt
rm statsdc.txt
for rfile in *.preparse
do
	
	cp $rfile soar-game.script
	rfilename=${rfile%$suffix}
	#b="zf4gpt5r9o83jm"
	#if [ "$rfilename" \< "$b" ]; then
	#	echo $rfilename
	#	continue
	#fi
	#echo "oops"
	java SentencesToSoar soar-game.script
	cp soar-game.soar /home/jrkirk/rosie2/rosie-project/rosie/agent/_agent/language-comprehension/comprehension/test-sentences/
	/home/jrkirk/rosie2/rosie-project/soar/out/./cli < game-data-agent.soar
	cp stats.txt results6/$rfilename$stats
	cp statsmax.txt results6/$rfilename$maxstats
	cp stats.csv results6/$rfilename$csv
	cp s1.txt results6/$rfilename$topstate
	cp soar-game.script results6/$rfilename$dialog
	
	cp pertaskstats.txt results6/$rfilename$pertaskstats
	cp smemuse.txt results6/$rfilename$smem
	cp statsdc.txt results6/$rfilename$dc
	
	
	rm pertaskstats.txt
	rm smemuse.txt
	rm statsdc.txt
	
done