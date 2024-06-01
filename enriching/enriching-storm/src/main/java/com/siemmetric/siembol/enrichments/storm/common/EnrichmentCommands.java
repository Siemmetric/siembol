package com.siemmetric.siembol.enrichments.storm.common;

import com.siemmetric.siembol.enrichments.common.EnrichmentCommand;

import java.util.ArrayList;
/**
 * A serializable object for representing list of enrichment commands
 *
 * <p>This class implements serializable interface and is used for representing list of enrichment commands.
 *
 * @author Marian Novotny
 */
public class EnrichmentCommands extends ArrayList<EnrichmentCommand> {
    private static final long serialVersionUID = 1L;
}
