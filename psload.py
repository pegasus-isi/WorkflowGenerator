import random
from workflow import *

def main(file, N=800, n=5):
    w = Workflow(name="psload", description="""Pan-STARRS database loading workflow (Figure 13 in Ramakrishnan and Gannon)""")
    
    end = Job(id="end", namespace="psload", name="End", runtime=10*SECONDS)
    w.addJob(end)
    
    for i in range(1,N+1):
        totalsize = 0
        preprocess = Job(id="preprocess%d"%i, namespace="psload", name="PreprocessCSV", runtime=5*SECONDS)
        w.addJob(preprocess)
        
        validateout = File(name="validate%d_out.dat"%i, size=100*MB)
        validate = Job(id="validate%d"%i, namespace="psload", name="ValidateLoadDB", runtime=5*SECONDS, outputs=[validateout])
        w.addJob(validate)
        end.addInput(validateout)
        
        for j in range(1,random.randint(1,n)+1):
            size = random.randint(1,100)*MB
            totalsize += size
            loadin = File(name="load%d.%d_in.dat"%(i,j), size=size)
            loadout = File(name="load%d.%d_out.dat"%(i,j), size=size)
            load = Job(id="load%d.%d"%(i,j), namespace="psload", name="LoadCSV", runtime=30*SECONDS, inputs=[loadin], outputs=[loadout])
            w.addJob(load)
            
            preprocess.addOutput(loadin)
            validate.addInput(loadout)
        
        indata = File(name="preprocess%d_in.dat"%i, size=totalsize)
        preprocess.addInput(indata)
    
    w.writeDAX(file)

if __name__ == '__main__':
    main("/dev/stdout", 50, 5)
