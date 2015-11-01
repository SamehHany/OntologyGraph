CREATE DATABASE Ontology

CREATE TABLE TelevisionSeason
(
	Id LONG
);

CREATE TABLE GrandPrix
(
	Id LONG,
	fastestDriverTeam LONG,
	secondTeam LONG,
	poleDriverTeam LONG,
	thirdTeam LONG,
	firstDriverTeam LONG
);

CREATE TABLE SoccerClub
(
	Id LONG,
	chairmanTitle LONG
);

CREATE TABLE Instrument
(
	Id LONG,
	musicians LONG
);

CREATE TABLE FilmFestival
(
	Id LONG,
	film LONG,
	Equivalent LONG,
	openingFilm LONG,
	closingFilm LONG
);

CREATE TABLE AnatomicalStructure
(
	Id LONG,
	graySubject VARCHAR(255),
	grayPage VARCHAR(255),
	vein LONG,
	drainsFrom LONG,
	Equivalent LONG,
	organSystem LONG,
	branchFrom LONG,
	precursor LONG,
	lymph LONG,
	drainsTo LONG,
	branchTo LONG,
	nerve LONG,
	artery LONG
);

CREATE TABLE Tournament
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE BeachVolleyballPlayer
(
	Id LONG
);

CREATE TABLE Artwork
(
	Id LONG,
	museum LONG
);

CREATE TABLE NobleFamily
(
	Id LONG,
	otherFamilyBranch LONG,
	mainFamilyBranch LONG,
	Equivalent LONG
);

CREATE TABLE Globularswarm
(
	Id LONG,
	radius_ly VARCHAR(255),
	v_hb VARCHAR(255)
);

CREATE TABLE Settlement
(
	Id LONG,
	mergedSettlement LONG,
	twinTown LONG,
	commune LONG,
	canton LONG
);

CREATE TABLE CyclingTeam
(
	Id LONG,
	uciCode VARCHAR(255)
);

CREATE TABLE RouteStop
(
	Id LONG,
	routePrevious LONG,
	routeNext LONG
);

CREATE TABLE Broadcaster
(
	Id LONG,
	shareDate VARCHAR(255),
	sisterStation LONG,
	broadcastNetwork LONG,
	formerBroadcastNetwork LONG,
	network LONG,
	Equivalent LONG
);

CREATE TABLE Place
(
	Id LONG,
	eastPlace LONG,
	land LONG,
	subregion LONG,
	northPlace LONG,
	locatedInArea LONG,
	northEastPlace LONG,
	flower LONG,
	northWestPlace LONG,
	previousEntity LONG,
	mainIsland LONG,
	namedByLanguage LONG,
	southPlace LONG,
	nextEntity LONG,
	subdivision LONG,
	closeTo LONG,
	westPlace LONG,
	bird LONG,
	southWestPlace LONG,
	hasInsidePlace LONG,
	tree LONG,
	supply LONG,
	southEastPlace LONG,
	hasOutsidePlace LONG
);

CREATE TABLE IceHockeyPlayer
(
	Id LONG,
	prospectLeague LONG,
	lchfDraftTeam LONG,
	khlDraftTeam LONG,
	whaDraftTeam LONG,
	prospectTeam LONG,
	Equivalent LONG
);

CREATE TABLE FormerMunicipality
(
	Id LONG,
	presentMunicipality LONG,
	municipalityAbsorbedBy LONG,
	Equivalent LONG
);

CREATE TABLE Document
(
	Id LONG,
	hasAnnotation LONG,
	Equivalent LONG,
	galleryItem LONG
);

CREATE TABLE TennisLeague
(
	Id LONG
);

CREATE TABLE MilitaryConflict
(
	Id LONG,
	isPartOfMilitaryConflict LONG
);

CREATE TABLE Q36180
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE CyclingLeague
(
	Id LONG
);

CREATE TABLE Gene
(
	Id LONG,
	geneLocation LONG,
	orthologousGene LONG,
	Equivalent LONG
);

CREATE TABLE River
(
	Id LONG,
	rightTributary LONG,
	leftTributary LONG,
	riverBranch LONG,
	riverBranchOf LONG
);

CREATE TABLE PopulatedPlace
(
	Id LONG,
	sheading LONG,
	unitaryAuthority LONG,
	oldProvince LONG,
	department LONG,
	parish LONG,
	largestSettlement LONG,
	largestCity LONG,
	arrondissement LONG,
	lieutenancyArea LONG,
	councilArea LONG,
	oldDistrict LONG,
	principalArea LONG,
	borough LONG,
	metropolitanBorough LONG,
	ceremonialCounty LONG,
	nisCode LONG,
	neighboringMunicipality LONG
);

CREATE TABLE Automobile
(
	Id LONG,
	Equivalent LONG,
	automobilePlatform LONG,
	fuelCapacity LONG,
	wheelbase LONG
);

CREATE TABLE TVEpisode
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Skyscraper
(
	Id LONG,
	contractor LONG,
	Equivalent LONG
);

CREATE TABLE Beach
(
	Id LONG
);

CREATE TABLE Work
(
	Id LONG,
	subsequentWork LONG,
	previousWork LONG,
	basedOn LONG
);

CREATE TABLE Person
(
	Id LONG,
	detractor LONG,
	ethnicity LONG,
	colleague LONG,
	dubber LONG,
	seiyu LONG,
	spouse LONG,
	copilote LONG,
	friend LONG,
	collaboration LONG,
	relation LONG,
	coemperor LONG,
	cousurper LONG,
	sibling LONG,
	influencedBy LONG,
	student LONG,
	opponent LONG,
	parent LONG,
	influenced LONG,
	partner LONG,
	usurper LONG,
	relative LONG
);

CREATE TABLE Mountain
(
	Id LONG,
	Equivalent LONG,
	parentMountainPeak LONG
);

CREATE TABLE OlympicResult
(
	Id LONG,
	winterAppearances LONG,
	summerAppearances LONG,
	otherAppearances LONG
);

CREATE TABLE Infrastructure
(
	Id LONG,
	caterer LONG,
	length LONG,
	road LONG,
	subsequentInfrastructure LONG,
	previousInfrastructure LONG
);

CREATE TABLE Organisation
(
	Id LONG,
	parentOrganisation LONG,
	affiliation LONG,
	childOrganisation LONG
);

CREATE TABLE Vodka
(
	Id LONG
);

CREATE TABLE Animal
(
	Id LONG,
	sire LONG,
	damsire LONG,
	grandsire LONG,
	Equivalent LONG,
	dam LONG
);

CREATE TABLE MilitaryUnit
(
	Id LONG,
	Equivalent LONG,
	commandStructure LONG,
	march LONG
);

CREATE TABLE Engine
(
	Id LONG,
	displacement LONG,
	acceleration LONG,
	powerOutput LONG,
	topSpeed LONG,
	co2Emission LONG,
	torqueOutput LONG
);

CREATE TABLE Canal
(
	Id LONG,
	Equivalent LONG,
	hasJunctionWith LONG
);

CREATE TABLE GolfLeague
(
	Id LONG
);

CREATE TABLE Saint
(
	Id LONG,
	suppreddedDate DATE,
	beatifiedDate DATE,
	canonizedDate DATE,
	Equivalent LONG
);

CREATE TABLE ChemicalSubstance
(
	Id LONG,
	boilingPoint LONG,
	solventWithBadSolubility LONG,
	solventWithMediocreSolubility LONG,
	notSolubleIn LONG,
	density LONG,
	meltingPoint LONG,
	solventWithGoodSolubility LONG
);

CREATE TABLE SpaceMission
(
	Id LONG,
	missionDuration LONG,
	spacestation LONG,
	previousMission LONG,
	spacecraft LONG,
	landingVehicle LONG,
	nextMission LONG
);

CREATE TABLE RouteOfTransportation
(
	Id LONG,
	routeJunction LONG,
	routeStart LONG,
	routeEnd LONG
);

CREATE TABLE Case
(
	Id LONG
);

CREATE TABLE Legislature
(
	Id LONG,
	house LONG
);

CREATE TABLE TrackList
(
	Id LONG
);

CREATE TABLE PoliticalParty
(
	Id LONG,
	splitFromParty LONG,
	Equivalent LONG,
	mergedIntoParty LONG
);

CREATE TABLE PlayWright
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE HumanGeneLocation
(
	Id LONG
);

CREATE TABLE MusicGenre
(
	Id LONG,
	musicFusionGenre LONG,
	Equivalent LONG,
	Equivalent LONG,
	derivative LONG,
	musicSubgenre LONG,
	stylisticOrigin LONG
);

CREATE TABLE LifeCycleEvent
(
	Id LONG
);

CREATE TABLE RocketEngine
(
	Id LONG
);

CREATE TABLE TennisPlayer
(
	Id LONG,
	rankingsDoubles VARCHAR(255),
	rankingsSingles VARCHAR(255),
	Equivalent LONG
);

CREATE TABLE Article
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE SquashPlayer
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Garden
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Governor
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE BrownDwarf
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Intercommunality
(
	Id LONG,
	shape LONG,
	Equivalent LONG
);

CREATE TABLE InformationObject
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Q212980
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE PersonalEvent
(
	Id LONG
);

CREATE TABLE Q6607
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Q2159907
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE MotorsportRacer
(
	Id LONG
);

CREATE TABLE ArtificialSatellite
(
	Id LONG
);

CREATE TABLE Q1930187
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Q24354
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Q3957
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Zoo
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE BritishRoyalty
(
	Id LONG
);

CREATE TABLE Artist
(
	Id LONG,
	training LONG,
	afiAward LONG,
	Equivalent LONG,
	cesarAward LONG,
	academyAward LONG,
	baftaAward LONG,
	tonyAward LONG,
	goyaAward LONG,
	mentor LONG,
	polishFilmAward LONG,
	emmyAward LONG,
	grammyAward LONG,
	filmFareAward LONG,
	goldenGlobeAward LONG,
	disciple LONG,
	gaudiAward LONG,
	associatedAct LONG
);

CREATE TABLE centimetre
(
	Id LONG
);

CREATE TABLE Location
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Vicar
(
	Id LONG
);

CREATE TABLE SoccerManager
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Parish,_Deanery
(
	Id LONG,
	diocese LONG
);

CREATE TABLE Q25324
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Q13561328
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE BowlingLeague
(
	Id LONG
);

CREATE TABLE RugbyClub
(
	Id LONG
);

CREATE TABLE Q38720
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Bacteria
(
	Id LONG
);

CREATE TABLE WomensTennisAssociationTournament
(
	Id LONG
);

CREATE TABLE GeologicalPeriod
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Q32815
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Agent
(
	Id LONG,
	regionalCouncil LONG,
	Equivalent LONG,
	ideology LONG,
	owns LONG,
	roleInEvent LONG,
	generalCouncil LONG
);

CREATE TABLE Manga
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE Reptile
(
	Id LONG
);

CREATE TABLE Q174782
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE ClericalAdministrativeRegion
(
	Id LONG,
	placeOfWorship LONG
);

CREATE TABLE RaceHorse
(
	Id LONG,
	jockey LONG
);

CREATE TABLE Q515716
(
	Id LONG,
	Equivalent LONG
);

CREATE TABLE ElectionDiagram
(
	Id LONG
);

CREATE TABLE Skater
(
	Id LONG,
	Equivalent LONG
);

