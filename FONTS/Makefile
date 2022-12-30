CLASS_INPUT =  ./src/Prop/Dominio/*.java \
	       ./src/Prop/Dominio/Busqueda/*.java \
	       ./src/Prop/Exceptions/*.java \
	       ./src/Prop/Persistencia/*.java \
	       ./src/Prop/Presentacion/*.java \

CLASS_OUTPUT = ../EXE/out/

JAR_OUTPUT = ../EXE/

all: 
	javac -d $(CLASS_OUTPUT) $(CLASS_INPUT)
	jar cvfm $(JAR_OUTPUT)PROP22-2.jar Manifest.txt -C $(CLASS_OUTPUT) .
exec:	
	java -jar PROP22-2.jar
clean:
	rm -r ../EXE/out/*
	
distclean:
	rm -r ../EXE/*
