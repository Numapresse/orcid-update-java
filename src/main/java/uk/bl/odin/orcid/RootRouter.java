package uk.bl.odin.orcid;

import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.resource.Directory;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import uk.bl.odin.orcid.rest.CacheFilter;
import uk.bl.odin.orcid.rest.MetadataFetchResource;
import uk.bl.odin.orcid.rest.OrcidAuthURLResource;
import uk.bl.odin.orcid.rest.OrcidIdentifierResource;
import uk.bl.odin.orcid.rest.OrcidProfileResource;
import uk.bl.odin.orcid.rest.OrcidSearchResource;
import uk.bl.odin.orcid.rest.OrcidTokenResource;
import uk.bl.odin.orcid.rest.OrcidWorkCreationResource;
import uk.bl.odin.orcid.rest.report.DatacentreDOIPrefixResource;
import uk.bl.odin.orcid.rest.report.OrcidDataCentreReportResource;
import uk.bl.odin.orcid.rest.report.PublisherDOIPrefixResource;

/**
 * RESTlet routing and general application configuration.
 */
public class RootRouter extends Router {

	private static final Logger log = Logger.getLogger(RootRouter.class.getName());

	/**
	 * Configures endpoints, sets up OrcidOAuthClient & OrcidWorkProvider and
	 * places them in the RESTlet Context
	 * 
	 * Routes:
	 * <ul>
	 * <li>"/orcid/token" convert authz codes from orcid into authz tokens</li>
	 * <li>"/orcid/requests" generate a authz request url (?redirect=true to
	 * bounce user)</li>
	 * <li>"/orcid/requests/{originalRef}" generate a authz request url with
	 * originalRef as state param (?redirect=true to bounce user)</li>
	 * <li>"/orcid/{orcid}/orcid-works/create" create a work by posting
	 * OrcidWork XML (requires ?token= orcid oauth token)</li>
	 * <li>"/meta/{id}" fetch metadata from external source - use (?json) for
	 * raw form</li>
	 * <li>"/webjars" webjars endpoint - example:
	 * /webjars/bootstrap/3.0.3/css/bootstrap.min.css</li>
	 * </ul>
	 * 
	 * 
	 */
	public RootRouter(Context context) {
		super(context);

		// import work rest routes
		this.attach("/orcid/token", OrcidTokenResource.class);
		//this.attach("/orcid/requests/{originalRef}", OrcidAuthURLResource.class);
		this.attach("/orcid/requests", OrcidAuthURLResource.class);//?originalRef optional

		this.attach("/orcid/search", OrcidSearchResource.class);

		this.attach("/orcid/{orcid}/orcid-works/create", OrcidWorkCreationResource.class);
		this.attach("/orcid/{orcid}", OrcidProfileResource.class);
		this.attach("/meta", MetadataFetchResource.class); //the configured type

		// reporting rest routes
		this.attach("/report/datatable", OrcidDataCentreReportResource.class);
		this.attach("/doiprefix/publishers", PublisherDOIPrefixResource.class);
		this.attach("/doiprefix/datacentres", DatacentreDOIPrefixResource.class);
		// identifier enumerations
		this.attach("/identifier/{type}", OrcidIdentifierResource.class);

		// add a webjars listener(see
		// http://demeranville.com/controlling-the-cache-headers-for-a-restlet-directory/
		final Directory dir = new Directory(getContext(), "clap://class/META-INF/resources/webjars");
		Filter cache = new CacheFilter(getContext(), dir);
		this.attach("/webjars", cache);

		log.info("RootRouter created, ready to serve");
	}

}
