CREATE DATABASE Ontology

CREATE TABLE South_Asian_Company
(
	Id LONG,
	Superclass LONG
);

CREATE TABLE Production_Company
(
	Id LONG
);

CREATE TABLE Northern_America
(
	Id LONG,
	Disjoint LONG,
	Disjoint LONG
);

CREATE TABLE Territory
(
	Id LONG,
	Subclass LONG,
	Subclass LONG,
	Subclass LONG,
	containsCompanyCountry LONG
);

CREATE TABLE Southeastern_Asia
(
	Id LONG,
	Disjoint LONG,
	Disjoint LONG,
	Disjoint LONG,
	Disjoint LONG,
	Superclass LONG,
	Disjoint LONG,
	Disjoint LONG,
	Disjoint LONG,
	Disjoint LONG
);

CREATE TABLE Person
(
	Id LONG,
	Subclass LONG,
	birthDate varchar(255),
	Subclass LONG,
	Subclass LONG,
	Subclass LONG,
	Subclass LONG,
	Subclass LONG,
	Subclass LONG,
	Subclass LONG,
	birthName varchar(255)
);

CREATE TABLE Online_retailer
(
	Id LONG
);

CREATE TABLE Musical_Entertainment
(
	Id LONG,
	Superclass LONG
);

CREATE TABLE Sensible
(
	Id LONG,
	Subclass LONG,
	Subclass LONG
);

CREATE TABLE Information
(
	Id LONG,
	Subclass LONG,
	Subclass LONG,
	Superclass LONG
);

