package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.ReportService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mindtree.leafservice3.domain.Report;
import com.mindtree.leafservice3.repository.ReportRepository;
import com.mindtree.leafservice3.repository.search.ReportSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Report}.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportRepository reportRepository;

    private final ReportSearchRepository reportSearchRepository;

    public ReportServiceImpl(ReportRepository reportRepository, ReportSearchRepository reportSearchRepository) {
        this.reportRepository = reportRepository;
        this.reportSearchRepository = reportSearchRepository;
    }

    /**
     * Save a report.
     *
     * @param report the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Report save(Report report) {
        log.debug("Request to save Report : {}", report);
        Report result = reportRepository.save(report);
        reportSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the reports.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Report> findAll() {
        log.debug("Request to get all Reports");
        return reportRepository.findAll();
    }


    /**
     * Get one report by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findOne(Long id) {
        log.debug("Request to get Report : {}", id);
        return reportRepository.findById(id);
    }

    /**
     * Delete the report by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Report : {}", id);
        reportRepository.deleteById(id);
        reportSearchRepository.deleteById(id);
    }

    @Override
    public void pdfGenerator() {
        log.debug("Request to generate pdf Report ");
        FileOutputStream file = null;
        
        try {
            file = new FileOutputStream(new File("test.pdf"));
            Document document = new Document();
            PdfWriter.getInstance(document, file);
            PdfWriter writer = PdfWriter.getInstance(document, file);
            document.open();
            document.add(new Paragraph("List Example")); 
            document.add(new Paragraph("A Hello World PDF document."));
            document.add(new Paragraph("A second paragraph."));
            document.add(new Paragraph("A third paragraph."));
         
            document.close();
            writer.close();
            System.out.println("file wrote successfully");
        }
        catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
    }

    /**
     * Search for the report corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Report> search(String query) {
        log.debug("Request to search Reports for query {}", query);
        return StreamSupport
            .stream(reportSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
