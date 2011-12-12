from workflow import *

def main(file, N):
    """Southeastern Coastal Ocean Observing and Prediction Program (SCOOP) workflow (Figure 4 in Ramakrishnan and Gannon)"""
    w = Workflow()
    
    inputs = []
    outputs = []
    parents = []
    
    for i in range(1,N+1):
        
        adcircin = File(name="adcirc_in%d.dat" % i, size=275*MB)
        inputs.append(adcircin)
        
        adcircout = File(name="adcirc_out%d.dat" % i, size=162*MB)
        outputs.append(adcircin)
        
        adcirc = Job(id="adcirc%d" % i, namespace="scoop", name="Adcirc", runtime=900*SECONDS, cores=16, inputs=[adcircin], outputs=[adcircout])
        w.addJob(adcirc)
        parents.append(adcirc)
    
    # The runtime, cores and outputs are not specified in the paper
    sc = Job(id="sc", namespace="scoop", name="PostProcessing", runtime=1*SECONDS, parents=parents, inputs=outputs)
    w.addJob(sc)
    
    w.write(file)

if __name__ == '__main__':
    main("/dev/stdout", 5)
