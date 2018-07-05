package com.mycompany;

import org.apache.log4j.BasicConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MyJsoupFindById {
    private static Logger LOGGER = LoggerFactory.getLogger(MyJsoupFindById.class);
    private final static String CHARSET_NAME = "utf8";
    private final static String TARGET_ELEMENT_ID = "make-everything-ok-button";
    private final static List<String> ELEMENTS_CLASS_NAMES = Arrays.asList("btn-success", "test-link-ok");

    public static void main(String[] args) {
        BasicConfigurator.configure();
        String originalFilePath = args[0];
        String changedFilePath = args[1];

        Optional<Element> buttonOpt = findElementById(new File(originalFilePath), TARGET_ELEMENT_ID);

        String originalContent = buttonOpt
                .map(button -> button.childNodes()
                        .stream()
                        .collect(Collectors.toList())
                        .get(0))
                .get().toString();

        Optional<List<Elements>> elementsByClassName = findElementsByClassName(new File(changedFilePath), ELEMENTS_CLASS_NAMES);
        List<Element> elementsByContent = findElementsByContent(elementsByClassName, originalContent);
        printElementPath(elementsByContent);
    }

    private static Optional<Element> findElementById(File htmlFile, String targetElementId) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            return Optional.of(doc.getElementById(targetElementId));

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private static Optional<List<Elements>> findElementsByClassName(File htmlFile, List<String> classNames) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            return Optional.of(classNames
                    .stream()
                    .map(className -> doc.getElementsByClass(className))
                    .collect(Collectors.toList()));

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }


    /**
     * Filtering given elements using find elements by given content.
     * Possibility to return couple of elements in case of multiple elements on the page.
     */

    private static List<Element> findElementsByContent(Optional<List<Elements>> optionalElements, String content) {
        content = content.trim();
        final String finalContent = content;


        // Filtering collection to avoid empty nodes
        List<Elements> collect = optionalElements.get().stream()
                .filter(elements -> elements.size() > 0)
                .collect(Collectors.toList());

        Elements elementList = new Elements();

        collect.forEach(elements -> elements
                .forEach(element -> elementList.add(element)));

        // Before retuning filtering collection ro avoid nodes without required content
        return elementList.stream()
                .filter(element -> element.text().equals(finalContent))
                .collect(Collectors.toList());
    }


    private static void printElementPath(List<Element> givenElements) {
        List<Elements> pathElementsSequence = new ArrayList<>();

        // Fills path sequences list with elements
        givenElements.forEach(element -> {
            Elements elements = new Elements();
            pathElementsSequence.add(elements);
            do {
                elements.add(element);
                element = element.parent();
            } while (element.parent() != null);
        });

        List<List<StringBuilder>> collect = pathElementsSequence.stream()
                .map(elements -> elements.stream().map(path ->
                        new StringBuilder()
                                .append(path.tag())
                                .append("[")
                                .append(path.elementSiblingIndex())
                                .append("]")
                                .append(" -> "))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        // Reverse order needs, because original direct of paths was from element to top of document.
        collect.forEach(list -> Collections.reverse(list));

        List<StringBuilder> pathToStringList = new ArrayList<>();

        collect.forEach(element -> {
            StringBuilder stringBuilder = new StringBuilder();
            pathToStringList.add(stringBuilder);
            element.forEach(element1 -> stringBuilder.append(element1));
        });

        pathToStringList.forEach(string -> LOGGER.info(string.toString()));
    }
}
