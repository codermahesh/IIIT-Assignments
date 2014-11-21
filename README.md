Wikipedia Search Engine

Part 1:
It included IR system preprocessing [case-folding,stemming,lemmatization,normalization]
followed by parametric indexing on wikipedia dump of size 40GB.
Two level indexing for keyword and Title list is generated at end of processing.
System developed is Parser-Indexer mapped on Producer-Consumer exploiting full cpu utilization
Performance : 100MB Processing in less than 50 sec.

Part 2:
It included creation of search model based on TF-IDF ranking with defined weighting
on indexing fields [outlink,title,text,info], query processing is full text search 
results into top 10 titles of document ranked higher.
Performance: Query results produced in less than 1 sec delay.

Addition: nearest Word suggestion for wrong keywords [time consuming process]
