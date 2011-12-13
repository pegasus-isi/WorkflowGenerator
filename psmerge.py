import random
from workflow import *

def main(file, N=16, nlo=300, nhi=600):
    w = Workflow(name="psmerge", description="""Pan-STARRS database merging workflow (Figure 14 in Ramakrishnan and Gannon)""")
    
    update = Job(id="update", namespace="psmerge", name="UpdateProductionDB", runtime=1*HOURS)
    w.addJob(update)
    
    for i in range(1,N+1):
        preprocessin1 = File(name="preprocess%d_in1.dat"%i, size=100*MB)
        preprocessin2 = File(name="preprocess%d_in2.dat"%i, size=100*MB)
        preprocess = Job(id="preprocess%d"%i, namespace="psmerge", name="ColdDB/LoadDB/Preprocess", runtime=5*MINUTES, inputs=[preprocessin1, preprocessin2])
        w.addJob(preprocess)
        
        validateout = File(name="validate%d_out.dat"%i, size=2.03*TB)
        validate = Job(id="validate%d"%i, namespace="psmerge", name="ValidateMerge", runtime=1*MINUTES, outputs=[validateout])
        w.addJob(validate)
        
        update.addInput(validateout)
        
        for j in range(1,random.randint(nlo,nhi)+1):
            mergein1 = File(name="merge%d.%d_in1.dat"%(i,j), size=100*MB)
            mergein2 = File(name="merge%d.%d_in2.dat"%(i,j), size=2*TB)
            mergeout = File(name="merge%d.%d_out.dat"%(i,j), size=2*TB)
            merge = Job(id="merge%d.%d"%(i,j), namespace="psmerge", name="MergeDB", runtime=3*HOURS, inputs=[mergein1, mergein2], outputs=[mergeout])
            w.addJob(merge)
            
            preprocess.addOutput(mergein1)
    
    w.writeDAX(file)

if __name__ == '__main__':
    main("/dev/stdout",2,1,10)
