package uk.ac.ebi.hca.core;

public class SubmissionEnvelope {

    private Dataset dataset;

    private String submissionUrl;

    public SubmissionEnvelope(String submissionUrl) {
        this.submissionUrl = submissionUrl;
    }

    public void put(Dataset dataset) {
        this.dataset = dataset;
    }

}
