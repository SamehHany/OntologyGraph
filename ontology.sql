CREATE DATABASE Ontology

CREATE TABLE Entity
(
	Subclass varchar(255),
	Subclass varchar(255),
	hasItem varchar(255)
);

CREATE TABLE Lot
(
	batch varchar(255),
	hasLot varchar(255),
	fromStore varchar(255),
	externalCode varchar(255),
	hasStore varchar(255),
	cost varchar(255),
	toStore varchar(255),
	hasWarehouse varchar(255),
	price varchar(255)
);

CREATE TABLE Document
(
	unitQty INT,
	Superclass varchar(255)
);

CREATE TABLE PhysicalStore
(
	Superclass varchar(255)
);

