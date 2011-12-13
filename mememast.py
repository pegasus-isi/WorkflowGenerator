from workflow import *

def main(file):
    w = Workflow(name="mememast", description="""MEME-MAST bioinformatics workflow (Figure 9 in Ramakrishnan and Gannon)""")
    
    memein = File(name="meme_in.dat", size=100*KB)
    
    memeout = File(name="meme_out.dat", size=150*KB)
    meme = Job(id="meme", namespace="mememast", name="MEME", runtime=60*SECONDS, inputs=[memein], outputs=[memeout])
    w.addJob(meme)
    
    mastout = File(name="mast_out.dat", size=200*KB)
    mast = Job(id="mast", namespace="mememast", name="MAST", runtime=60*SECONDS, inputs=[memeout], outputs=[mastout])
    w.addJob(mast)
    
    w.writeDAX(file)

if __name__ == '__main__':
    main("/dev/stdout")
