package com.example.retirementCalculator.config;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.io.StringWriter;

public class XmlConfig {

    public static String toXml(LifestyleDeposit deposit) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(LifestyleDeposit.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(deposit, writer);
        return writer.toString();
    }

    public static LifestyleDeposit fromXml(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(LifestyleDeposit.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (LifestyleDeposit) unmarshaller.unmarshal(new StringReader(xml));
    }
}
