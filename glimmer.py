import sys
from main import Main
from workflow import *

def glimmer():
    w = Workflow(name="glimmer", description="""Glimmer bioinformatics workflow (Figure 6 in Ramakrishnan and Gannon)""")
    
    orfin = File(name="orf_in.dat", size=8.8*MB)
    orfout = File(name="orf_out.dat", size=27*KB)
    long_orfs = Job(id="long_orfs", namespace="glimmer", name="Long_orfs", runtime=2*SECONDS, inputs=[orfin], outputs=[orfout])
    w.addJob(long_orfs)
    
    extractout = File(name="extract_out.dat", size=1.6*MB)
    extract = Job(id="extract", namespace="glimmer", name="extract", runtime=1*SECONDS, inputs=[orfout], outputs=[extractout])
    w.addJob(extract)
    
    icmout = File(name="icm_out.dat", size=1.35*MB)
    build_icm = Job(id="build_icm", namespace="glimmer", name="build_icm", runtime=5*SECONDS, inputs=[extractout], outputs=[icmout])
    w.addJob(build_icm)
    
    glimmerout = File(name="glimmer_out.dat", size=9.9*MB)
    glimmer2 = Job(id="glimmer2", namespace="glimmer", name="glimmer2", runtime=90*SECONDS, inputs=[icmout], outputs=[glimmerout])
    w.addJob(glimmer2)
    
    return w

def main(*args):
    class Glimmer(Main):
        def genworkflow(self, options):
            return glimmer()
    
    Glimmer().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
