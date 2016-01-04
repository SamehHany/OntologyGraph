PREFIX : <http://www.movieontology.org/2009/11/09/movieontology.owl#>
PREFIX mo: <http://www.movieontology.org/2009/10/01/movieontology.owl#>
PREFIX dbpedia: <http://dbpedia.org/ontology/>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
SELECT DISTINCT ?title ?prod_year
WHERE {
  ?x a mo:Movie;
       mo:title ?title;
       dbpedia:productionStartYear ?prod_year;
       mo:belongsToGenre ?genre.
  ?genre a mo:SciFi_and_Fantasy.
  FILTER (?prod_year>=1985 && ?prod_year<=1990)
}

ORDER BY ?title
