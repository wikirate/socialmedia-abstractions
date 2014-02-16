package eu.socialsensor.framework.abstractions.flickr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.stats.Stats;
import com.flickr4java.flickr.tags.Tag;

import eu.socialsensor.framework.common.domain.Feed;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.SocialNetworkSource;

/**
 * Class that holds the information regarding the flickr photo
 * @author ailiakop
 * @email  ailiakop@iti.gr
 */
public class FlickrItem extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1200195286847838121L;

	public FlickrItem(String id, Operation operation) {
		super(SocialNetworkSource.Flickr.toString(), operation);
		setId(SocialNetworkSource.Flickr+"#"+id);
	}
	
	public FlickrItem(Photo photo) {
		super(SocialNetworkSource.Flickr.toString(), Operation.NEW);
		if (photo == null || photo.getId() == null) return;
		
		//Id
		id = SocialNetworkSource.Flickr + "#" + photo.getId();
		//SocialNetwork Name
		streamId = SocialNetworkSource.Flickr.toString();
		//Timestamp of the creation of the photo
		publicationTime = photo.getDatePosted().getTime();
		//Title of the photo
		if(photo.getTitle()!=null){
			if(photo.getTitle().length()>100){
				title = photo.getTitle().subSequence(0, 100)+"...";
				text = photo.getTitle();
			}
			else{
				title = photo.getTitle();
				text = photo.getTitle();
			}
		}
		//Description of the photo
		description = photo.getDescription();
		//Tags of the photo

		Collection<Tag> photoTags = photo.getTags();
		if (photoTags != null) {
			tags = new String[photoTags.size()];
			int i = 0;
			for(Tag tag : photoTags) {
				tags[i++] = tag.getValue();
			}
		}
		//User that posted the photo
        User user = photo.getOwner();
        if(user != null) {
                streamUser = new FlickrStreamUser(user);
                uid = streamUser.getId();
        }
		//Location
		if(photo.hasGeoData()){
			
			GeoData geo = photo.getGeoData();
			
			double latitude = (double)geo.getLatitude();
			double longitude = (double) geo.getLongitude();
			
			location = new Location(latitude, longitude);
		}
		
		//Popularity
		
		//Getting the photo
	
		String url = null;
		try {
			String thumbnail = photo.getMediumUrl();
			if(thumbnail==null) {
				thumbnail = photo.getThumbnailUrl();
			}
			URL mediaUrl = null;
			if((url = photo.getLargeUrl()) != null) {
				mediaUrl = new URL(url);
			
			}
			else if ((url = photo.getMediumUrl()) != null) {
				mediaUrl = new URL(url);
			}
			
			if(mediaUrl!=null) {
				//url
				MediaItem mediaItem = new MediaItem(mediaUrl);
				
				String mediaId = SocialNetworkSource.Flickr + "#"+photo.getId(); 
				
				//id
				mediaItem.setId(mediaId);
				//SocialNetwork Name
				mediaItem.setStreamId(streamId);
				//Reference
				mediaItem.setRef(id);
				//Type 
				mediaItem.setType("image");
				//Time of publication
				mediaItem.setPublicationTime(publicationTime);
				//Author
				mediaItem.setUser(streamUser);
				mediaItem.setUserId(uid);
				//PageUrl
				mediaItem.setPageUrl(photo.getUrl());
				//Thumbnail
				mediaItem.setThumbnail(thumbnail);
				//Title
				mediaItem.setTitle(title);
				//Description
				mediaItem.setDescription(description);
				//Tags
				mediaItem.setTags(tags);
				//Location
				mediaItem.setLocation(location);
				//Popularity
				Stats statistics = photo.getStats();
				if(statistics != null) {
					mediaItem.setViews((long) statistics.getViews());
					mediaItem.setLikes((long) statistics.getFavorites());
					mediaItem.setComments((long) statistics.getComments());
				}
				else{
					mediaItem.setComments(new Long(photo.getComments()));
					Stats stats = photo.getStats();
					if(stats != null) {
						mediaItem.setViews((long)stats.getViews());
					}
				}
				
				//Store mediaItems and their ids 
				mediaItems.add(mediaItem);
				mediaIds.add(mediaId);
				
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public FlickrItem(Photo photo, FlickrStreamUser streamUser, Feed itemFeed) {
		this(photo);

		//User that posted the photo
		this.streamUser = streamUser;
		uid = streamUser.getId();
		//Feed that retrieved the post
		feed = itemFeed;
		feedType = itemFeed.getFeedtype().toString();
				
		for(MediaItem mItem : mediaItems) {
			mItem.setUserId(uid);
		}

	}
	
	public FlickrItem(Photo photo, Feed itemFeed) {
		this(photo);

		feed = itemFeed;
		feedType = itemFeed.getFeedtype().toString();

	}

}
