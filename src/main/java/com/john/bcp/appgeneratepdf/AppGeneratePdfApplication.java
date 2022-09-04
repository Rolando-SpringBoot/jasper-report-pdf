package com.john.bcp.appgeneratepdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;

@RestController
@SpringBootApplication
public class AppGeneratePdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppGeneratePdfApplication.class, args);
	}

	@GetMapping(value = "/generate-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<Resource> generatePdf()
			throws UnsupportedEncodingException, JRException, FileNotFoundException {
		String json = "{\"fecha\":\"Miércoles, 27 de Abril del 2022\","
				+ "\"nombre\":\"ELISA SOFIA VILLA ARCEO\",\"producto\":\"Crédito Hipotecario\","
				+ "\"finalidad\":\"COMPRA DE INMUEBLE - VIVIENDA\",\"monto\":\"687750.00\","
				+ "\"plazo\":\"25 años\",\"periodoGracia\":\"5 meses\",\"numeroCuotas\":\"14\","
				+ "\"vigencia\":\"180\"}";

		JsonDataSource jsonDataSource = new JsonDataSource(
				new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8.name())));
		JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream("src" + File.separator
				+ "main" + File.separator + "resources" + File.separator + "prefacturacion.jrxml"));

		Map<String, Object> map = new HashMap<>();
		JasperPrint report = JasperFillManager.fillReport(compileReport, map, jsonDataSource);

		byte[] data = JasperExportManager.exportReportToPdf(report);
		InputStream inputStream = new ByteArrayInputStream(data);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=invoice.pdf");
		httpHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));

		return new ResponseEntity<>(new InputStreamResource(inputStream), httpHeaders, HttpStatus.OK);
	}

}
