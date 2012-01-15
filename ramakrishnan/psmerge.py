import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import random
from generator import *

def psmerge(N=16, nlow=300, nhigh=600):
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
        
        for j in range(1,random.randint(nlow,nhigh)+1):
            mergein1 = File(name="merge%d.%d_in1.dat"%(i,j), size=100*MB)
            mergein2 = File(name="merge%d.%d_in2.dat"%(i,j), size=2*TB)
            mergeout = File(name="merge%d.%d_out.dat"%(i,j), size=2*TB)
            merge = Job(id="merge%d.%d"%(i,j), namespace="psmerge", name="MergeDB", runtime=3*HOURS, inputs=[mergein1, mergein2], outputs=[mergeout])
            w.addJob(merge)
            
            validate.addInput(mergeout)
            preprocess.addOutput(mergein1)
    
    return w

def main(*args):
    class PSMerge(Main):
        def setoptions(self, parser):
            self.parser.add_option("-N", "--numpreprocess", dest="N", metavar="N", type="int", default=16,
                help="Number of preprocess jobs [default: %default]")
            self.parser.add_option("-L", "--nlow", dest="nlow", metavar="L", type="int", default=300,
                help="Minimum number of merge jobs per preprocess job  [default: %default] (range is L to H)")
            self.parser.add_option("-H", "--nhigh", dest="nhigh", metavar="H", type="int", default=600,
                help="Minimum number of merge jobs per preprocess job  [default: %default] (range is L to H)")
        
        def genworkflow(self, options):
            return psmerge(options.N, options.nlow, options.nhigh)
    
    PSMerge().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
