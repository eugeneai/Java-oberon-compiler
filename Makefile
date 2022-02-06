.PHONY: compile run

M=mvn

compile:
	$(M) package

run: compile
	$(M) exec:java