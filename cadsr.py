from workflow import *

def main(file):
    w = Workflow(name="cadsr", description="""Cancer Data Standards Repository workflow (Figure 12 in Ramakrishnan and Gannon)""")
    
    projectsin = File(name="projects_in.dat", size=10*MB)
    projectsout = File(name="projects_out.dat", size=10*MB)
    projects = Job(id="findProjects", namespace="cadsr", name="findProjects", runtime=5*SECONDS, inputs=[projectsin], outputs=[projectsout])
    w.addJob(projects)
    
    classesout = File(name="classes_out.dat", size=15*MB)
    classes = Job(id="findClassesInProjects", namespace="cadsr", name="findClassesInProjects", runtime=5*SECONDS, inputs=[projectsout], outputs=[classesout])
    w.addJob(classes)
    
    metadataout = File(name="metadata_out.dat", size=10*MB)
    metadata = Job(id="findSemanticMetadata", namespace="cadsr", name="findSemanticMetadata", runtime=5*SECONDS, inputs=[projectsout, classesout], outputs=[metadataout])
    w.addJob(metadata)
    
    searchout = File(name="search_out.dat", size=15*MB)
    search = Job(id="searchLogicConcept", namespace="cadsr", name="searchLogicConcept", runtime=5*SECONDS, inputs=[metadataout], outputs=[searchout])
    w.addJob(search)
    
    w.computeDataDependencies()
    w.writeDAX(file)

if __name__ == '__main__':
    main("/dev/stdout")
