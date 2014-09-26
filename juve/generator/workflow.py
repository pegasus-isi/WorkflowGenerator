
KB = 1024
MB = 1024*KB
GB = 1024*MB
TB = 1024*GB

SECONDS = 1
MINUTES = 60*SECONDS
HOURS = 60*MINUTES

COLORS = [
        "#1b9e77",
        "#d95f02",
        "#7570b3",
        "#e7298a",
        "#66a61e",
        "#e6ab02",
        "#a6761d",
        "#666666",
        "#8dd3c7",
        "#bebada",
        "#fb8072",
        "#80b1d3",
        "#fdb462",
        "#b3de69",
        "#fccde5",
        "#d9d9d9",
        "#bc80bd",
        "#ccebc5",
        "#ffed6f",
        "#ffffb3"
]


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
    def __init__(self, name=None, description=None):
        self.name = name
        self.description = description
        self.jobs = set()
    
    def addJob(self, job):
        self.jobs.add(job)
    
    def _computeDataDependencies(self):
        """This sets all the parent-child dependencies based on the input and output files of the jobs"""
        # Prepare a mapping of file -> job that generated it
        sources = {}
        for j in self.jobs:
            for k in j.outputs:
                if k in sources:
                    raise Exception("Duplicate source for %s" % k)
                sources[k] = j
        
        # Use source mapping to look up the job that produces 
        # each input for every job, and make the job dependent
        # upon it.
        for j in self.jobs:
            for i in j.inputs:
                if i in sources:
                    j.addParent(sources[i])
    
    def writeDAX(self, filename):
        self._computeDataDependencies()
        
        childCount = reduce(lambda x,y: x+y, [1 for x in self.jobs if len(x.parents)>0], 0)
        jobCount = len(self.jobs)
        
        f = open(filename, "w")
        f.write('<?xml version="1.0" encoding="UTF-8"?>\n')
        f.write('<!-- %s -->\n' % (self.description))
        f.write('<adag name="%s" jobCount="%d" fileCount="0" childCount="%d">\n' % (self.name, jobCount, childCount))
        
        for j in self.jobs:
            f.write('\t<job id="%s" namespace="%s" name="%s" runtime="%s" cores="%d">\n' % (j.id, j.namespace, j.name, j.runtime, j.cores))
            for i in j.inputs:
                f.write('\t\t<uses file="%s" link="input" size="%d"/>\n' % (i.name, i.size))
            for o in j.outputs:
                f.write('\t\t<uses file="%s" link="output" size="%d"/>\n' % (o.name, o.size))
            f.write('\t</job>\n')
        
        for j in self.jobs:
            if len(j.parents) > 0:
                f.write('\t<child ref="%s">\n' % (j.id))
                for p in j.parents:
                    f.write('\t\t<parent ref="%s"/>\n' % (p.id))
                f.write('\t</child>\n')
        
        f.write('</adag>\n')
        f.close()
    
    def writeDOT(self, filename, width=8.0, height=10.0):
        self._computeDataDependencies()
        
        next_color = 0  # Keep track of next color
        xforms = {} # Keep track of transformation names to assign colors
        
        f = open(filename,'w')
        
        f.write("""digraph dag {
    size="%s,%s"
    ratio=fill
    node [shape=ellipse, style=filled]
    edge [arrowhead=normal, arrowsize=1.0]
    \n""" % (width,height))
        
        for j in self.jobs:
            if j.name not in xforms:
                xforms[j.name] = next_color
                next_color += 1
                # Just in case we run out of colors
                next_color = min(len(COLORS)-1, next_color)
            color = xforms[j.name]
            f.write('\t"%s" [color="%s",label="%s"]\n' % (j.id,COLORS[color],j.name))
        
        f.write('\n')
        
        for j in self.jobs:
            for p in j.parents:
                f.write('\t"%s" -> "%s"\n' % (p.id, j.id))
        
        f.write("}\n")
        f.close()


if __name__ == '__main__':
    w = Workflow(name="Test", description="""Test Workflow""")
    
    inp = File(name="bar.in", size=1024)
    outp = File(name="bar.out", size=1024)
    
    j = Job(id="foo1", namespace="foo", name="bar", runtime=102.4, inputs=[inp], outputs=[outp])
    w.addJob(j)
    
    j2 = Job(id="foo2", namespace="foo", name="bar", runtime=1024, inputs=[outp], parents=[j])
    w.addJob(j2)
    
    w.writeDAX("/dev/stdout")
    w.writeDOT("/dev/stdout")
