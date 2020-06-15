package com.github.jmchilton.blend4j.galaxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.testng.annotations.Test;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.HistoryDatasetElement;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionElementResponse;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.ElementResponse;
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.client.WebTarget;

import java.util.Map;

import org.testng.Assert;

public class Examples {
  public static void main(final String[] args) throws Exception {
    runExamples();
  }

  @Test
  public static void runExamples() throws Exception {
    final String[] exampleMethods = new String[] {
      "listHistories",
      "listLibraryContents",
      "levelsOfAbstraction",
      "createDatasetCollectionListPaired",
      "createPrivateDataLibrary"
    };
    for(final String exampleMethod : exampleMethods) {
      runExample(exampleMethod);
    }
  }

  public static void listHistories(final String url, final String apiKey) {
    GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(url, apiKey);
    HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
    for(History history : historiesClient.getHistories()) {
      String name = history.getName();
      String id = history.getId();
      String message = String.format("Found history with name %s and id %s", name, id);
      System.out.println(message);
    }
  }

  public static void listLibraryContents(final String url, final String apiKey) {
    // Find a data library by name and print its contents 
    final GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(url, apiKey);
    final LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
    final List<Library> libraries = librariesClient.getLibraries();
    Library testLibrary = null;
    for(final Library library : libraries) {
      if(library.getName().equals("test-library")) {
        testLibrary = library;
      }
    }
    if(testLibrary == null) {
      return;
    }
    for(final LibraryContent content : librariesClient.getLibraryContents(testLibrary.getId())) {
      final String type = content.getType(); // file or folder
      final String name = content.getName();
      final String id = content.getId();
      final String message = String.format("Found library content of type %s with name %s and id %s", type, name, id);
      System.out.println(message);
    }
  }

  public static void levelsOfAbstraction(final String url, final String apiKey) {
    // Most API methods have corresponding blend4j methods for dealing with 
    // both low-level request and parsed POJO responses. You can also use the method
    // galaxyInstance.getWebResource() to access the low-level Jersey APIs directly.
    final GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(url, apiKey);
    final LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();

    // Highest level of abstraction, deal with POJO responses
    final Library testLibrary1 = new Library("test1");
    final Library persistedLibrary1 = librariesClient.createLibrary(testLibrary1);

    // Deal with Jersey ClientResponse object in case want to check return status, etc...
    final Library testLibrary2 = new Library("test2");
    final ClientResponse response2 = librariesClient.createLibraryRequest(testLibrary2);
    if(response2.getStatus() == 200) {
      final Library persistedLibrary2 = response2.getEntity(Library.class);
      // ...
    }

    // Use Jersey directly (with POJOs)
    final WebTarget webResource3 = galaxyInstance.getWebResource();
    final Library testLibrary3 = new Library("test3");
    final ClientResponse response3 = webResource3
            .path("libraries")
            .type(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .post(ClientResponse.class, testLibrary3);
    final Library persistedLibrary3 = response3.getEntity(Library.class);

    // Use Jersey directly (no POJOs)
    final WebTarget webResource4 = galaxyInstance.getWebResource();
    final ClientResponse response4 = webResource4
            .path("libraries")
            .type(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .post(ClientResponse.class, "{\"name\": \"test4\"}");
    final String jsonResponse4 = response4.getEntity(String.class);
    System.out.println("JSON response is: " + jsonResponse4);
  }

  public static void createPrivateDataLibrary(final String url, final String apiKey) {
    final GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(url, apiKey);

    final String email = "alice@example.com";

    // Create data library
    final Library library = new Library("Example library for " + email);
    final LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
    final Library persistedLibrary = librariesClient.createLibrary(library);

    // Copy example directory into library
    final FilesystemPathsLibraryUpload upload = new FilesystemPathsLibraryUpload();
    final LibraryContent rootFolder = librariesClient.getRootFolder(persistedLibrary.getId());
    upload.setFolderId(rootFolder.getId());
    upload.setContent("test-data/variant_detection");
    librariesClient.uploadFilesystemPathsRequest(persistedLibrary.getId(), upload);

    // Obtain user object
    User owner = null;
    final UsersClient usersClient = galaxyInstance.getUsersClient();
    for(final User user : usersClient.getUsers()) {
      if(user.getEmail().equals(email)) {
        owner = user;
        break;
      }
    }
    if(owner == null) {
      // In order to create users like this - use_remote_user must be enabled
      // in the Galaxy instance's universe_wsgi.ini options.
      owner = usersClient.createUser(email);
    }

    // Obtain user role
    Role ownersPrivateRole = null;
    final RolesClient rolesClient = galaxyInstance.getRolesClient();
    for(final Role role : rolesClient.getRoles()) {
      if(role.getName().equals(email)) {
        ownersPrivateRole = role;
        break;
      }
    }
    final String ownersPrivateRoleId = ownersPrivateRole.getId();

    // Set data library permissions
    final LibraryPermissions permissions = new LibraryPermissions();
    permissions.getAccessInRoles().add(ownersPrivateRoleId);
    permissions.getAddInRoles().add(ownersPrivateRoleId);
    permissions.getManageInRoles().add(ownersPrivateRoleId);
    permissions.getModifyInRoles().add(ownersPrivateRoleId);
    librariesClient.setLibraryPermissions(persistedLibrary.getId(), permissions);
  }

  public static void runWorkflow(final String url, final String apiKey) {
    final GalaxyInstance instance = GalaxyInstanceFactory.get(url, apiKey);
    final WorkflowsClient workflowsClient = instance.getWorkflowsClient();

    // Find history
    final HistoriesClient historyClient = instance.getHistoriesClient();
    History matchingHistory = null;
    for(final History history : historyClient.getHistories()) {
      if(history.getName().equals("TestHistory1")) {
        matchingHistory = history;
      }
    }
    Assert.assertNotNull(matchingHistory);
    String input1Id = null;
    String input2Id = null;
    for(final HistoryContents historyDataset : historyClient.showHistoryContents(matchingHistory.getId())) {
      if(historyDataset.getName().equals("Input1")) {
        input1Id = historyDataset.getId();
      }
      if(historyDataset.getName().equals("Input2")) {
        input2Id = historyDataset.getId();
      }
    }

    Workflow matchingWorkflow = null;
    for(Workflow workflow : workflowsClient.getWorkflows()) {
      if(workflow.getName().equals("TestWorkflow1")) {
        matchingWorkflow = workflow;
      }
    }

    final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(matchingWorkflow.getId());
    String workflowInput1Id = null;
    String workflowInput2Id = null;
    for(final Map.Entry<String, WorkflowInputDefinition> inputEntry : workflowDetails.getInputs().entrySet()) {
      final String label = inputEntry.getValue().getLabel();
      if(label.equals("WorkflowInput1")) {
        workflowInput1Id = inputEntry.getKey();
      }
      if(label.equals("WorkflowInput2")) {
        workflowInput2Id = inputEntry.getKey();
      }
    }

    final WorkflowInputs inputs = new WorkflowInputs();
    inputs.setDestination(new WorkflowInputs.ExistingHistory(matchingHistory.getId()));
    inputs.setWorkflowId(matchingWorkflow.getId());
    inputs.setInput(workflowInput1Id, new WorkflowInputs.WorkflowInput(input1Id, WorkflowInputs.InputSourceType.HDA));
    inputs.setInput(workflowInput2Id, new WorkflowInputs.WorkflowInput(input2Id, WorkflowInputs.InputSourceType.HDA));
    final WorkflowOutputs output = workflowsClient.runWorkflow(inputs);
    System.out.println("Running workflow in history " + output.getHistoryId());
    for(String outputId : output.getOutputIds()) {
      System.out.println("  Workflow writing to output id " + outputId);
    }
  }
  
  /**
   * Example of building a dataset collection within a history of a list of paired fastq reads.
   * This example assumes the existence of a history 'TestHistoryCollection' with the files:
   *  file1_1.fastq, file1_2.fastq, file2_1.fastq, file2_2.fastq.
   * @param url  The url to the Galaxy instance.
   * @param apiKey  The apiKey for the Galaxy instance.
   */
  public static void createDatasetCollectionListPaired(final String url, final String apiKey) {
    GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(url, apiKey);
    
    // Find history named 'TestHistoryCollection'
    final HistoriesClient historyClient = galaxyInstance.getHistoriesClient();
    History matchingHistory = null;
    for(final History history : historyClient.getHistories()) {
      if(history.getName().equals("TestHistoryCollection")) {
        matchingHistory = history;
      }
    }
    Assert.assertNotNull(matchingHistory);
    
    // Obtain paired fastq file history ids
    String file1_forewardId = null;
    String file1_reverseId = null;
    String file2_forewardId = null;
    String file2_reverseId = null;
    for(final HistoryContents historyDataset : historyClient.showHistoryContents(matchingHistory.getId())) {
      if(historyDataset.getName().equals("file1_1.fastq")) {
        file1_forewardId = historyDataset.getId();
      }
      if(historyDataset.getName().equals("file1_2.fastq")) {
        file1_reverseId = historyDataset.getId();
      }
      if(historyDataset.getName().equals("file2_1.fastq")) {
        file2_forewardId = historyDataset.getId();
      }
      if(historyDataset.getName().equals("file2_2.fastq")) {
        file2_reverseId = historyDataset.getId();
      }
    }
    
    HistoryDatasetElement file1_forward = new HistoryDatasetElement();
    file1_forward.setId(file1_forewardId);
    file1_forward.setName("forward");
    
    HistoryDatasetElement file1_reverse = new HistoryDatasetElement();
    file1_reverse.setId(file1_reverseId);
    file1_reverse.setName("reverse");
    
    // Create an object to link together the forward and reverse reads for file1
    CollectionElement file1 = new CollectionElement();
    file1.setName("file1");
    file1.setCollectionType("paired");
    file1.addCollectionElement(file1_forward);
    file1.addCollectionElement(file1_reverse);
    
    HistoryDatasetElement file2_forward = new HistoryDatasetElement();
    file2_forward.setId(file2_forewardId);
    file2_forward.setName("forward");
    
    HistoryDatasetElement file2_reverse = new HistoryDatasetElement();
    file2_reverse.setId(file2_reverseId);
    file2_reverse.setName("reverse");
    
    // Create an object to link together the forward and reverse reads for file2
    CollectionElement file2 = new CollectionElement();
    file2.setName("file2");
    file2.setCollectionType("paired");
    file2.addCollectionElement(file2_forward);
    file2.addCollectionElement(file2_reverse);
    
    // Create an object used to create the list of paired reads
    CollectionDescription collectionDescription = new CollectionDescription();
    collectionDescription.setCollectionType("list:paired");
    collectionDescription.setName("ListPairedReads");
    collectionDescription.addDatasetElement(file1);
    collectionDescription.addDatasetElement(file2);
    
    // Builds a dataset collection within Galaxy named 'ListPairedReads'
    CollectionResponse collectionResponse =
        historyClient.createDatasetCollection(matchingHistory.getId(), collectionDescription);
    
    // Print information on the newly created dataset collection
    System.out.println("New dataset collection created historyId=" + collectionResponse.getHistoryId() 
        + ", collectionId=" + collectionResponse.getId() + ", name=" + collectionResponse.getName());
    System.out.println("Contents are:");
    
    // Prints out information on newly created dataset
    printDatasetCollectionRecursive(collectionResponse, "");
  }
  
  /**
   * Iterates over and prints dataset collections, including any sub-collections within the dataset.
   * @param collectionResponse  The collection to print.
   * @param level  The level to print this dataset as (indentation for printing).
   */
  private static void printDatasetCollectionRecursive(CollectionResponse collectionResponse, String level) {
    for (CollectionElementResponse element : collectionResponse.getElements()) {
      System.out.println(level + "element " + element.getElementIdentifier() + 
          ", name=" + element.getElementIdentifier() + ", type=" + element.getElementType());
      
      ElementResponse elementResponse = element.getResponseElement();
      
      // Case 1: The element contains a collection of files.
      if (elementResponse instanceof CollectionResponse) {
        printDatasetCollectionRecursive((CollectionResponse)elementResponse, level + "\t");
      }
      
      // Case 2: The element contains a single dataset
      else if (elementResponse instanceof Dataset) {
        Dataset dataset = (Dataset)elementResponse;
        
        System.out.println(level + "\t" + "dataset " + dataset.getName() + ", type=" 
            + dataset.getDataType() + ", fileSize=" + dataset.getFileSize() +
            ", id=" + dataset.getId());
      }
    }
  }

  private static void runExample(final String methodName) throws Exception {
    final Method method = findExampleMethod(methodName);
    final String testInstanceUrl = TestGalaxyInstance.getTestInstanceUrl();
    final String testApiKey = TestGalaxyInstance.getTestApiKey();
    method.invoke(null, testInstanceUrl, testApiKey);
  }

  private static Method findExampleMethod(final String methodName) {
    Method matchingMethod = null;
    for(final Method method : Examples.class.getMethods()) {
      if(method.getName().equals(methodName) && Modifier.isStatic(method.getModifiers())) {
        matchingMethod = method;
      }
    }
    return matchingMethod;
  }
}
