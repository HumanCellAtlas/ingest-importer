# Manual Test Curl Commands

```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'content-type: application/json' \
  -d '{}'
```

## Adding JSON

### Project
```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/projects \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'content-type: application/json' \
  -d '  {
    "describedBy": "https://schema.humancellatlas.org/type/project/5.0.1/project",
    "schema_version": "5.0.1",
    "schema_type": "project",
    "project_core": {
      "project_shortname": "Q4_DEMO-project_PRJNA248302",
      "project_title": "Q4_DEMO-Single cell RNA-seq of primary human glioblastomas",
      "project_description": "Q4_DEMO-We report transcriptomes from 430 single glioblastoma cells isolated from 5 individual tumors and 102 single cells from gliomasphere cells lines generated using SMART-seq. In addition, we report population RNA-seq from the five tumors as well as RNA-seq from cell lines derived from 3 tumors (MGH26, MGH28, MGH31) cultured under serum free (GSC) and differentiated (DGC) conditions. This dataset highlights intratumoral heterogeneity with regards to the expression of de novo derived transcriptional modules and established subtype classifiers. Overall design: Operative specimens from five glioblastoma patients (MGH26, MGH28, MGH29, MGH30, MGH31) were acutely dissociated, depleted for CD45+ inflammatory cells and then sorted as single cells (576 samples). Population controls for each tumor were isolated by sorting 2000-10000 cells and processed in parallel (5 population control samples). Single cells from two established cell lines, GBM6 and GBM8, were also sorted as single cells (192 samples). SMART-seq protocol was implemented to generate single cell full length transcriptomes (modified from Shalek, et al Nature 2013) and sequenced using 25 bp paired end reads. Single cell cDNA libraries for MGH30 were resequenced using 100 bp paired end reads to allow for isoform and splice junction reconstruction (96 samples, annotated MGH30L). Cells were also cultured in serum free conditions to generate gliomasphere cell lines for MGH26, MGH28, and MGH31 (GSC) which were then differentiated using 10% serum (DGC). Population RNA-seq was performed on these samples (3 GSC, 3 DGC, 6 total). The initial dataset included 875 RNA-seq libraries (576 single glioblastoma cells, 96 resequenced MGH30L, 192 single gliomasphere cells, 5 tumor population controls, 6 population libraries from GSC and DGC samples). Data was processed as described below using RSEM for quantification of gene expression. 5,948 genes with the highest composite expression either across all single cells combined (average log2(TPM)>4.5) or within a single tumor (average log2(TPM)>6 in at least one tumor) were included. Cells expressing less than 2,000 of these 5,948 genes were excluded. The final processed dataset then included 430 primary single cell glioblastoma transcriptomes, 102 single cell transcriptomes from cell lines(GBM6,GBM8), 5 population controls (1 for each tumor), and 6 population libraries from cell lines derived from the tumors (GSC and DGC for MGH26, MGH28 and MGH31). The final matrix (GBM_data_matrix.txt) therefore contains 5948 rows (genes) quantified in 543 samples (columns). Please note that the samples which are not included in the data processing are indicated in the sample description field.",
      "describedBy": "https://schema.humancellatlas.org/core/project/5.0.0/project_core",
      "schema_version": "5.0.0"
    }
  }'
```

### BioMaterial

Donor Organism:
```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/biomaterials \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'content-type: application/json' \
  -d ' {
    "describedBy": "https://schema.humancellatlas.org/type/biomaterial/5.0.0/donor_organism",
    "schema_version": "5.0.0",
    "schema_type": "donor_organism",
    "biomaterial_core": {
      "biomaterial_id": "Q4_DEMO-donor_MGH30",
      "biomaterial_name": "Q4 DEMO donor MGH30",
      "ncbi_taxon_id": [
        9606
      ],
      "describedBy": "https://schema.humancellatlas.org/core/biomaterial/5.0.0/biomaterial_core",
      "schema_version": "5.0.0"
    },
    "genus_species": [
      "Homo sapiens"
    ],
    "is_living": "yes"
  }'
```

Specimen From Organism
```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/biomaterials \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'content-type: application/json' \
  -d ' {
           "describedBy": "https://schema.humancellatlas.org/type/biomaterial/5.0.0/specimen_from_organism",
           "schema_version": "5.0.0",
           "schema_type": "specimen_from_organism",
           "biomaterial_core": {
             "biomaterial_id": "Q4_DEMO-sample_SAMN02797092",
             "biomaterial_name": "Q4_DEMO-Single cell mRNA-seq_MGH30_A01",
             "ncbi_taxon_id": [
               9606
             ],
             "has_input_biomaterial": "Q4_DEMO-donor_MGH30",
             "supplementary_files": [
               "Q4_DEMO-protocol"
             ],
             "describedBy": "https://schema.humancellatlas.org/core/biomaterial/5.0.0/biomaterial_core",
             "schema_version": "5.0.0"
           },
           "genus_species": [
             "Homo sapiens"
           ],
           "organ": "brain",
           "organ_part": "astrocyte",
           "disease": [
             "glioblastoma"
           ]
         }'
```

Processes
```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/processes \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'Cache-Control: no-cache' \
  -H 'Postman-Token: f1971ffe-a708-466e-e5c9-93f39479f5a9' \
  -H 'content-type: application/json' \
  -d '  {
    "describedBy": "https://schema.humancellatlas.org/type/process/biomaterial_collection/5.0.0/enrichment_process",
    "schema_version": "5.0.0",
    "schema_type": "enrichment_process",
    "process_core": {
      "process_id": "enrichment1",
      "describedBy": "https://schema.humancellatlas.org/core/process/5.0.0/process_core",
      "schema_version": "5.0.0"
    },
    "enrichment_method": "FACS",
    "process_type": "enrichment process"
  }'
```

```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/processes \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'Cache-Control: no-cache' \
  -d '{
    "describedBy": "https://schema.humancellatlas.org/type/process/sequencing/5.0.0/library_preparation_process",
    "schema_version": "5.0.0",
    "schema_type": "library_preparation_process",
    "process_core": {
      "process_id": "preparation1",
      "describedBy": "https://schema.humancellatlas.org/core/process/5.0.0/process_core",
      "schema_version": "5.0.0"
    },
    "input_nucleic_acid_molecule": "polyA RNA",
    "library_construction_approach": "Smart-seq2",
    "end_bias": "5 prime end bias",
    "strand": "unstranded",
    "process_type": "nucleic acid librarly construction process",
    "protocol_ids": "library_protocol1"
  }'
```

```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/processes \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'Cache-Control: no-cache' \
  -d ' {
    "describedBy": "https://schema.humancellatlas.org/type/process/sequencing/5.0.0/sequencing_process",
    "schema_version": "5.0.0",
    "schema_type": "sequencing_process",
    "process_core": {
      "process_id": "assay1",
      "describedBy": "https://schema.humancellatlas.org/core/process/5.0.0/process_core",
      "schema_version": "5.0.0"
    },
    "instrument_manufacturer_model": "Illumina HiSeq 2500",
    "paired_ends": "yes",
    "process_type": "single cell sequencing process",
    "protocol_ids": "Q4_DEMO-protocol"
  }'
```

Protocols
```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/protocols \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'content-type: application/json' \
  -d '  {
    "describedBy": "https://schema.humancellatlas.org/type/protocol/5.0.0/protocol",
    "schema_version": "5.0.0",
    "schema_type": "protocol",
    "protocol_core": {
      "protocol_id": "Q4_DEMO-protocol",
      "document": "Q4_DEMO-protocol.pdf",
      "describedBy": "https://schema.humancellatlas.org/core/protocol/5.0.0/protocol_core",
      "schema_version": "5.0.0"
    },
    "protocol_type": "single cell sequencing protocol"
  }'
```

```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/protocols \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'Cache-Control: no-cache' \
  -H 'content-type: application/json' \
  -d '  {
    "describedBy": "https://schema.humancellatlas.org/type/protocol/5.0.0/protocol",
    "schema_version": "5.0.0",
    "schema_type": "protocol",
    "protocol_core": {
      "protocol_id": "library_protocol1",
      "describedBy": "https://schema.humancellatlas.org/core/protocol/5.0.0/protocol_core",
      "schema_version": "5.0.0"
    },
    "protocol_type": "library construction protocol"
  }'
```

Files
```
curl -X POST \
  http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5a995c86153b8b00063e98e9/files \
  -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg' \
  -H 'content-type: application/json' \
  -d '  {
    "describedBy": "https://schema.humancellatlas.org/type/file/5.0.0/sequence_file",
    "schema_version": "5.0.0",
    "schema_type": "sequence_file",
    "file_core": {
      "file_name": "R2.fastq.gz",
      "file_format": "fastq.gz",
      "describedBy": "https://schema.humancellatlas.org/core/file/5.0.0/file_core",
      "schema_version": "5.0.0"
    },
    "lane_index": 1.0,
    "read_length": 225.0,
    "biomaterial_id": "Q4_DEMO-sample_SAMN02797092",
    "sequencing_process_id": "assay_1"
  }'
```