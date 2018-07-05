# smart xml analyzer
Hello.
This is my implementation of smart aml analyzer.

The main idea is fetch the path of element.
Programm get two files with element as parameters, finds wanted element in first file, then find path to the element in another file.
Program has opportunity to fetch and give multiple paths in case of multiple elemets present on page.
Elements finding by text and class.

First of all program find element by id from original file.
Then geting text from element.
Then finiding elements on the page by this text with given classes as criteria. 
Then program gets new file and looks for this elements.
After that calculating ang printing paths.
