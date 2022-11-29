# README.md

## Pre-requisites

### PostgreSQL

The program expects a running local Postgres instance and a Postgres database ```image_analyzer```, with a table ```images```.

The ```images``` table is defined as

	CREATE TABLE IF NOT EXISTS images (
	    id uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
	    url TEXT,
	    label TEXT,
	    objects TEXT ARRAY
	);

In production code I would use Flyway to create the table and add migrations, I didn't do this here in the interest of time.

## Start

The program can be run with ```sbt run``` in the ```ImageAnalyzer``` directory. sbt will download the required dependencies.
The API is available at port 8080.
