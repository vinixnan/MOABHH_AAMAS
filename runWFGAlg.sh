#!/bin/bash
problems="WFG1 WFG2 WFG3 WFG4 WFG5 WFG6 WFG7 WFG8 WFG9"
algs="NSGAII IBEA SPEA2"
rm -f "runMain.txt"
rm -f "erros"

function addToExecution {
    problem=$1
    obj=$2
    filename="fixedvoterMOABHH_saida_"$problem"_"$obj"_"$alg
    filenameer="errfixedvoterMOABHH_saida_"$problem"_"$obj"_"$alg
    
    rm ./$filename
    rm ./$filenameer
                    
    echo "java -Xms1024m -Xmx1024m -cp target/MOABHH-1.0-SNAPSHOT.jar:target/lib/* br.usp.poli.pcs.lti.moabhh.main.WFGAlgs $obj $alg $problem >> $filename 2>>$filenameer" >> "runMain.txt"
}  
 
for alg in $algs
do
    for problem in $problems
    do
        addToExecution $problem 2 $alg
    done

    for problem in $problems
    do
        addToExecution $problem 3 $alg
    done

    #for problem in $problems
    #do
    #    addToExecution $problem 5 $alg
    #done
done





cat "runMain.txt" | xargs -I CMD -P 5  bash -c CMD &
wait
