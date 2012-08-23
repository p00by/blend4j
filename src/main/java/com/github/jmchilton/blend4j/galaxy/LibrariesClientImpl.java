package com.github.jmchilton.blend4j.galaxy;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import com.github.jmchilton.blend4j.galaxy.beans.DirectoryLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.UrlLibraryUpload;
import com.sun.jersey.api.client.ClientResponse;

class LibrariesClientImpl extends ClientImpl implements LibrariesClient {

  LibrariesClientImpl(GalaxyInstanceImpl galaxyInstance) {
    super(galaxyInstance, "libraries");
  }

  public ClientResponse createLibraryRequest(final Library library) {
    return super.create(library);
  }

  public Library createLibrary(final Library library) {
    // should not be this complicated, I suspect library API is returning wrong thing. See galaxy issue #802
    return createLibraryRequest(library).getEntity(Library.class);
    //return readJson(createLibraryRequest(library).getEntity(String.class), new TypeReference<List<Library>>() {}).get(0);
  }

  public List<Library> getLibraries() {
    return get(new TypeReference<List<Library>>() {
    });
  }

  public ClientResponse uploadFileFromUrlRequest(final String libraryId, final UrlLibraryUpload upload) {
    return super.create(getWebResourceContents(libraryId), upload);
  }
  
  public ClientResponse uploadServerDirectoryRequest(final String libraryId, final DirectoryLibraryUpload upload) {
    return super.create(getWebResourceContents(libraryId), upload);
  }
  
  public ClientResponse uploadFilesystemPathsRequest(final String libraryId, final FilesystemPathsLibraryUpload upload) {
    return super.create(getWebResourceContents(libraryId), upload);    
  }

  public LibraryContent getRootFolder(final String libraryId) {
    final List<LibraryContent> libraryContents = getLibraryContents(libraryId);
    LibraryContent rootFolder = null;
    for(final LibraryContent content : libraryContents) {
      if("/".equals(content.getName())) {
        rootFolder = content;
        break;
      }
    }
    return rootFolder;
  }

  public List<LibraryContent> getLibraryContents(final String libraryId) {
    return get(getWebResourceContents(libraryId), new TypeReference<List<LibraryContent>>() {
    });
  }

  public ClientResponse uploadFileFromUrl(final String libraryId, final FilesystemPathsLibraryUpload upload) {
    return super.create(getWebResourceContents(libraryId), upload);
  }

  public ClientResponse setLibraryPermissions(String libraryId, LibraryPermissions permissions) {
    final String payload = write(permissions);
    System.out.println(payload);
    return super.create(getWebResource(libraryId).path("permissions"), payload);
  }

}
