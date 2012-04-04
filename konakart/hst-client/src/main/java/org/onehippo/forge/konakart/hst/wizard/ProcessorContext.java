package org.onehippo.forge.konakart.hst.wizard;

public interface ProcessorContext {

    /**
     * Provide seed information to this ProcessContext, usually
     * provided at time of workflow kickoff by the containing
     * workflow processor.
     *
     * @param seedObject - initial seed data for the workflow
     */
    void setSeedData(SeedData seedObject) throws ActivityException;

    /**
     * @return the seed information
     */
    SeedData getSeedData();

}
