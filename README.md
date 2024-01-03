# Context-Aware-Name-Recommendation-for-Field-Renaming

  - [General Introduction](#General-Introduction)
  - [Contents of the Replication Package](#Contents-of-the-Replication-Package)
  - [Requirements](#Requirements)
  - [Replicate the Evaluation](#How-to-Replicate-the-Evaluation)

 # General Introduction

This is the replication package for CARER, containing both tool and data that are requested by the replication. It also provides detailed instructions to replicate the evaluation.

  # Contents of the Replication Package

  /Dataset: Benchmark Datasets

  /Incoder: The implementation of Incoder

  /Intellij_IDEA: The implementation of Intellij_IDEA.

  /CARER: The implementation of CARER

  # Requirements

  - Java >= 17.0.7
  - Pyhton >= 3.9.0, tokenizers>=0.12

  # How to Replicate the Evaluation?

 ## Replicate the evaluation of CARER

   1. **Import project**

      `Go to *File* -> *import* ->*Existing Projects into Workspace*`

      Browse to the "CARER" directory

      `Click *OK*`
  
   2. **Run the experiment**

       `run *CARERTest.java*`

   ## Replicate the evaluation of Incoder
   1. **Clone replicate package to your local file system**

      `git clone https://github.com/anonymizez/Context-Aware-Name-Recommendation-for-Field-Renaming.git`

  2. **Import project**

      `git clone https://github.com/dpfried/incoder`
     
      `Go to *File* -> *Open...*(Pycharm,  incoder)`

     Browse to the "Incoder" directory

     `Click *OK*`

   3. **Run the experiment**

      `Right-click on the file and select *incoderInfillingScripts.py*(/Incoder/coder/incoderInfillingScripts.py)`

   ## Replicate the evaluation of Intellij IDEA

   1. **Import project**

      `Go to *File* -> *import* ->*Existing Projects into Workspace*`

      Browse to the "Intellij_IDEA" directory

      `Click *OK*`
  
   2. **Run the experiment**

       `run IDEATest.java`


