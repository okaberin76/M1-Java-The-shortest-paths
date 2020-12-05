# TP n°3: Les plsu courts chemins
# Rapport de BOURGEAUX Maxence, Groupe 1 Master 1 IWOCS

## Introduction

Ce TP consiste à utiliser un générateur aléatoire de graphe proposé par GraphStream, et de mesurer l'efficacité d'algorithmes de Dijkstra sur ces graphes.

Il faut donc tout d'abord consulter la documentation de RandomGenerator qui une classe de la librairie Graph Stream.
Cette librairie propose aussi un algorithme de Dijkstra très efficace, que nous allons tester sur des graphes aléatoirement
générés par RandomGenerator, puis comparer ces résultats avec une implémentation naïve de l'algorithme de Dijsktra.

Pour cela plusieurs critères de tests seront définis par la suite. Les résultats de ces tests sont consutables dans le fichier ficherResultat.dat