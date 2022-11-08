test:
	mvn clean test

lint:
	mvn checkstyle:check

clean:
	rm -rf $$(cat .gitignore)