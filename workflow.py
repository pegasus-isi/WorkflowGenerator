
KB = 1024
MB = 1024*KB
GB = 1024*MB
TB = 1024*GB

SECONDS = 1
MINUTES = 60*SECONDS
HOURS = 60*MINUTES

class File:
    def __init__(self, name, size=0):
        self.name = name
        self.size = size
    
    def __repr__(self):
        return "<File %s>" % self.name

class Job:
    def __init__(self, id, namespace=None, name=None, runtime=0, cores=1, parents=[], inputs=[], outputs=[]):
        self.id = id
        self.name = name
        self.namespace = namespace
        self.runtime = runtime
        self.cores = cores
        self.inputs = set(inputs)
        self.outputs = set(outputs)
        self.parents = set(parents)
    
    def addInput(self, file_):
        self.inputs.add(file_)
    
    def addOutput(self, file_):
        self.outputs.add(file_)
    
    def addParent(self, parent):
        self.parents.add(parent)
    
    def __repr__(self):
        return "<Job %s>" % self.name

class Workflow:
    def __init__(self):
        self.jobs = set()
        self.edges = set()
    
    def addJob(self, job):
        self.jobs.add(job)
    
    def write(self, filename):
        f = open(filename, "w")
        f.write('<?xml version="1.0" encoding="UTF-8"?>\n')
        f.write('<adag>\n')
        
        for j in self.jobs:
            f.write('\t<job id="%s" namespace="%s" name="%s" runtime="%s" cores="%s">\n' % (j.id, j.namespace, j.name, j.runtime, j.cores))
            for i in j.inputs:
                f.write('\t\t<uses file="%s" link="input" size="%s"/>\n' % (i.name, i.size))
            for o in j.outputs:
                f.write('\t\t<uses file="%s" link="output" size="%s"/>\n' % (o.name, o.size))
            f.write('\t</job>\n')
        
        for j in self.jobs:
            if len(j.parents) > 0:
                f.write('\t<child ref="%s">\n' % (j.id))
                for p in j.parents:
                    f.write('\t\t<parent ref="%s">\n' % (p.id))
                f.write('\t</child>\n')
        
        f.write('</adag>')
        f.close()

if __name__ == '__main__':
    w = Workflow()
    
    inp = File(name="bar.in", size=1024)
    outp = File(name="bar.out", size=1024)
    
    j = Job(id="foo1", namespace="foo", name="bar", runtime=1024, inputs=[inp], outputs=[outp])
    w.addJob(j)
    
    j2 = Job(id="foo2", namespace="foo", name="bar", runtime=1024, inputs=[outp], parents=[j])
    w.addJob(j2)
    
    w.write("/dev/stdout")
