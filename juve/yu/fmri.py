import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

def fmri(MIPS, level12, level45):
    w = Workflow(name="fmri", description="""fMRI workflow (Figure 4b in Yu et al)""")
    
    level2 = []
    for i in range(0, level12):
        align = Job(id="align_%d"%i, namespace="fmri", name="Align_wap", runtime=(300000/MIPS)*SECONDS)
        w.addJob(align)
        
        reslice = Job(id="reslice_%d"%i, namespace="fmri", name="reslice", runtime=(600000/MIPS)*SECONDS, parents=[align])
        w.addJob(reslice)
        
        level2.append(reslice)
    
    softmean = Job(id="softmean", namespace="fmri", name="softmean", runtime=(300000/MIPS)*SECONDS, parents=level2)
    w.addJob(softmean)
    
    for i in range(0, level45):
        slicer = Job(id="slicer_%d"%i, namespace="fmri", name="slicer", runtime=(300000/MIPS)*SECONDS, parents=[softmean])
        w.addJob(slicer)
        
        convert = Job(id="convert_%d"%i, namespace="fmri", name="convert", runtime=(600000/MIPS)*SECONDS, parents=[slicer])
        w.addJob(convert)
    
    return w

def main(*args):
    class FMRI(Main):
        def setoptions(self, parser):
            self.parser.add_option("-M", "--mips", dest="MIPS", metavar="MIPS", type="int", default=1200,
                help="MIPS of PE [default: %default]")
            self.parser.add_option("", "--level12", dest="level12", metavar="N", type="int", default=4,
                help="Number of jobs in level 1 and 2 [default: %default]")
            self.parser.add_option("", "--level45", dest="level45", metavar="N", type="int", default=3,
                help="Number of jobs in level 4 and 5 [default: %default]")
        
        def genworkflow(self, options):
            return fmri(options.MIPS, options.level12, options.level45)
    
    FMRI().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
