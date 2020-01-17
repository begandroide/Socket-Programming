#-----------------------------------------------------------------------
# Define compiler and compiler flag variables for Java
#-----------------------------------------------------------------------

# Flag for class path
J_DIRECTORY_CLASS_FLAG = -d
# Specify the path where to place .class files
J_DIRECTORY_CLASS_PATH = ./bin/
# Specify where to place generated class files
J_DIRECTORY_CLASS = $(J_DIRECTORY_CLASS_FLAG) $(J_DIRECTORY_CLASS_PATH)

# Flag for source path
J_DIRECTORY_SOURCE_FLAG = -sourcepath
# Specify the path where are the source files .java
J_DIRECTORY_SOURCE_PATH = ./src/
# Specify where are the source files .java
J_DIRECTORY_SOURCE = $(J_DIRECTORY_SOURCE_FLAG) $(J_DIRECTORY_SOURCE_PATH)

SRC = $(shell find ./src -type f)

OBJ = $(SRC:.java=.class)

JC = javac

JAVAC_COMPILE = $(JC) $(J_DIRECTORY_SOURCE) $(J_DIRECTORY_CLASS)

#-----------------------------------------------------------------------
# Default Java compilation
#-----------------------------------------------------------------------

default : bin $(OBJ) cpy

# Creación de .class en carpeta bin.
%.class: %.java
		@$(JAVAC_COMPILE) $*.java

# Creación directorio bin
cpy : 
	@cp -r ./src/resources ./bin/

# Creación directorio bin
bin : 
	@mkdir -p ./bin/

# Limpiar
clean :
	rm -Rfv bin/*

# Limpiar incluso bin
fullclean : clean
	rmdir ./bin
