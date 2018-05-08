#!/bin/bash

mvn clean compile
mvn exec:java -Dexec.mainClass="de.fau.cs.osr.amos.asepart.WebService"
