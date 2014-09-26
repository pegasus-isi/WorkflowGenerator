import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

# Askalon WIEN2K workflow

def wien2k(iterations, width, scale):
    def mkdist(dist):
        def wrapper():
            return scale*dist()
        return wrapper
    
    # Create runtime distributions
    # TODO Determine what the actual distributions should be
    dstagein = mkdist(UniformDistribution(1,100))
    dkgen = mkdist(UniformDistribution(1,100))
    dlapw0 = mkdist(UniformDistribution(1,100))
    dlapw1 = mkdist(UniformDistribution(1,100))
    dlapw2fermi = mkdist(UniformDistribution(1,100))
    dlapw2 = mkdist(UniformDistribution(1,100))
    dsumpara = mkdist(UniformDistribution(1,100))
    dlcore = mkdist(UniformDistribution(1,100))
    dmixer = mkdist(UniformDistribution(1,100))
    dconverged = mkdist(UniformDistribution(1,100))
    dstageout = mkdist(UniformDistribution(1,100))
    
    w = Workflow(name="wien2k", description="""WIEN2K workflow from Nadeem and Fahringer""")
    
    si = Job(id="stage_in", namespace="wien2k", name="StageIn", runtime=dstagein()*SECONDS)
    w.addJob(si)
    
    lastconverged = si
    for i in range(0,iterations):
        
        kgen = Job(id="kgen_%d"%i, namespace="wien2k", name="Kgen", runtime=dkgen()*SECONDS, parents=[lastconverged])
        w.addJob(kgen)
        
        lapw0 = Job(id="lapw0_%d"%i, namespace="wien2k", name="lapw0", runtime=dlapw0()*SECONDS, parents=[kgen])
        w.addJob(lapw0)
        
        level1 = []
        for j in range(0,width):
            lapw1 = Job(id="lapw1_%d_%d"%(i,j), namespace="wien2k", name="lapw1", runtime=dlapw1()*SECONDS, parents=[lapw0])
            w.addJob(lapw1)
            level1.append(lapw1)
        
        lapw2fermi = Job(id="lapw2fermi_%d"%i, namespace="wien2k", name="lapw2fermi", runtime=dlapw2fermi()*SECONDS, parents=level1)
        w.addJob(lapw2fermi)
        
        level2 = []
        for j in range(0,width):
            lapw2 = Job(id="lapw2_%d_%d"%(i,j), namespace="wien2k", name="lapw2", runtime=dlapw2()*SECONDS, parents=[lapw2fermi])
            w.addJob(lapw2)
            level2.append(lapw2)
        
        sumpara = Job(id="sumpara_%d"%i, namespace="wien2k", name="sumpara", runtime=dsumpara()*SECONDS, parents=level2)
        w.addJob(sumpara)
        
        lcore = Job(id="lcore_%d"%i, namespace="wien2k", name="lcore", runtime=dlcore()*SECONDS, parents=[sumpara])
        w.addJob(lcore)
        
        mixer = Job(id="mixer_%d"%i, namespace="wien2k", name="mixer", runtime=dmixer()*SECONDS, parents=[lcore])
        w.addJob(mixer)
        
        converged = Job(id="converged_%d"%i, namespace="wien2k", name="converged", runtime=dconverged()*SECONDS, parents=[mixer])
        w.addJob(converged)
        
        lastconverged = converged
    
    so = Job(id="stage_out", namespace="wien2k", name="StageOut", runtime=dstageout()*SECONDS, parents=[lastconverged])
    w.addJob(so)
    
    return w

def main(*args):
    class Application(Main):
        def setoptions(self, parser):
            self.parser.add_option("-i", "--iterations", dest="iterations", metavar="n", type="int", default=1,
                help="Number of iterations of convergence loop [default: %default]")
            self.parser.add_option("-w", "--width", dest="width", metavar="n", type="int", default=5,
                help="Width of workflow [default: %default]")
            self.parser.add_option("-s", "--scale", dest="scale", metavar="s", type="float", default=1.0,
                help="Runtime scale [default: %default]")
        
        def genworkflow(self, options):
            return wien2k(options.iterations, options.width, options.scale)
    
    Application().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
