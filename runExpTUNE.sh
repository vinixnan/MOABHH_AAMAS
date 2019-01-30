#!/bin/bash
problems="WFG1 WFG2 WFG3 WFG4 WFG5 WFG6 WFG7 WFG8 WFG9"



rm -f "runMain.txt"
rm -f "erros"

function addToExecution {
    problem=$1
    obj=$2
    k=$3
    l=$4
    qtdExp=40
   
    epsilons="40"
    deltas="40"
    betas="32 40 25"
    methods="Copeland Borda Kemeny"

    
    
    for beta in $betas
    do
        for method in $methods
        do
            for delta in $deltas
            do
                for epsilon in $epsilons
                do
                    filename="r_ep_spread_rni_reptrifixedvoterIMOABHH_saida_"$problem"_"$obj"_"$beta"_"$delta"_"$epsilon"_"$method
                    filenameer="r_ep_spread_rni_reptrierrfixedvoterIMOABHH_saida_"$problem"_"$obj"_"$beta"_"$delta"_"$epsilon"_"$method
                    rm ./$filename
                    rm ./$filenameer
                    number=1
                    while [ $number -le $qtdExp ]
                    do
                            echo "java -Xms1024m -Xmx1024m -cp target/MOABHH-1.0-SNAPSHOT.jar:target/lib/* br.usp.poli.pcs.lti.moabhh.main.CartagoJMetalOriginalMetrics $problem $obj $beta $delta $epsilon $k $l $method $number >> $filename 2>>$filenameer" >> "runMain.txt"
                            let number=$number+1;
                    done
                done
            done
        done
    done
    
}  
 
#addToExecution "DTLZ1" 2 0 6
#addToExecution "DTLZ2" 2 0 11
#addToExecution "DTLZ3" 2 0 11
#addToExecution "DTLZ4" 2 0 11
#addToExecution "DTLZ5" 2 0 11
#addToExecution "DTLZ6" 2 0 11
#addToExecution "DTLZ7" 2 0 21

for problem in $problems
do
    addToExecution $problem 2 2 20
done

#for problem in $problems
#do
#    addToExecution $problem 3 4 20
#done

#problems="UF1 UF2 UF3 UF4 UF5 UF6 UF7"
#for problem in $problems
#do
#    addToExecution $problem 2 0 30
#done


#problems="UF8 UF9 UF10"
#for problem in $problems
#do
#    addToExecution $problem 3 0 30
#done

#addToExecution "ZDT1" 2 0 30
#addToExecution "ZDT2" 2 0 30
#addToExecution "ZDT3" 2 0 30
#addToExecution "ZDT4" 2 0 10
#-addToExecution "ZDT5" 2 0 11
#addToExecution "ZDT6" 2 0 10

#addToExecution "DTLZ1" 3 0 7
#addToExecution "DTLZ2" 3 0 12
#addToExecution "DTLZ3" 3 0 12
#addToExecution "DTLZ4" 3 0 12
#addToExecution "DTLZ5" 3 0 12
#addToExecution "DTLZ6" 3 0 12
#addToExecution "DTLZ7" 3 0 22
#addToExecution "DTLZ5" 3 0 12


#addToExecution "CrashWorthiness" 3 0 0
#addToExecution "CarSideImpact" 3 0 0
#addToExecution "Water" 5 0 0
#addToExecution "Machining" 4 0 0

cat "runMain.txt" | xargs -I CMD -P 1  bash -c CMD &
wait
