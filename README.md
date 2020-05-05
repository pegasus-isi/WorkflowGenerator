**NOTE: This repository is not maintained any longer. We are in the process of developing a new Workflow Generator that will be released soon as part of the [WorkflowHub Project](https://workflowhub.org).** 

Synthetic Workflow Generators
=============================

This repository contains several synthetic workflow generators that can be used
to evaluate scheduling and provisioning algorithms for scientific workflow
management systems. These workflow generators are based on models of real
applications that have been parameterized with file size and task runtime data
from execution logs and publications that describe the workflows. If you use
this code to generate data that is used in a publication, please cite [1].
Please also cite the relevant publication for the workflows you use.

The 'bharathi' subdirectory contains a Java workflow generator that produces
synthetic workflows for the applications described in [2,3].

The 'juve' subdirectory contains a Python workflow generator that produces
synthetic workflows for the applications described in [4,5,6,7].

1. R. F. da Silva, W. Chen, G. Juve, K. Vahi, E. Deelman, "Community Resources
   for Enabling Research in Distributed Scientific Workflows", 10th IEEE
   International Conference on e-Science (eScience 2014), 2014.
2. S. Bharathi, A. Chervenak, E. Deelman, G. Mehta, M.-H. Su, and K. Vahi,
   "Characterization of Scientific Workflows", 3rd Workshop on Workflows in
   Support of Large Scale Science (WORKS 08), 2008.
3. G. Juve, A. Chervenak, E. Deelman, S. Bharathi, G. Mehta, and K. Vahi,
   "Characterizing and Profiling Scientific Workflows", Future Generation
   Computer Systems , 29:3, pp. 682–692, March 2013.
4. L. Ramakrishnan and D. Gannon, "A Survey of Distributed Workflow
   Characteristics and Resource Requirements", Indiana University Technical
   Report TR671, 2008.
5. J. Yu, R. Buyya, and C. K. Tham, "Cost-based Scheduling of Scientific
   Workflow Applications on Utility Grids", Proceedings of the 1st
   International Conference on e-Science and Grid Computing, 2005.
6. M. Rahman, S. Venugopal, and R. Buyya, "A Dynamic Critical Path
   Algorithm for Scheduling Scientific Workflow Applications on Global
   Grids", 3rd IEEE International Conference on e-Science and Grid
   Computing, 2007.
7. Farrukh Nadeem, Thomas Fahringer, "Using Templates to Predict
   Execution Time of Scientific Workflow Applications in the Grid",
   9th IEEE/ACM International Symposium on Cluster Computing and
   the Grid, 2009.

