package io.alanda.base.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "PMC_PROJECTTYPE")
public class PmcProjectType extends AbstractEntity {

  @Column(name = "NAME")
  String name;

  @Column(name = "IDNAME")
  String idName;

  @ManyToMany
  @JoinTable(name = "PMC_PROJECTTYPE_DOCUCONFIG", joinColumns = @JoinColumn(name = "PROJECTTYPE", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "DOCUCONFIG", referencedColumnName = "ID"))
  @OrderColumn(name = "SORTORDER")
  List<DocuConfig> docuConfigs;

  @ManyToMany
  @JoinTable(name = "PMC_PROJECTTYPE_CHILDTYPE", joinColumns = @JoinColumn(name = "PROJECTTYPE", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "CHILDTYPE", referencedColumnName = "GUID"))
  Collection<PmcProjectType> allowedChildTypes;

  /**
   * Tags dienen Primär zu Reporting zwecken. Hier wird definiert welche Tags in einem PmcProjekt verwendet werden
   * dürfen.
   */
  @Column(name = "ALLOWEDTAGS")
  String allowedTags;

  /**
   * Hier sind die Prozessdefinitionen eingetragen welche direkt im Projekt gestartet werden dürfen. Muss leer sein wenn
   * das Projekt Phasen hat.
   */
  @Column(name = "ALLOWEDPROCESSES")
  String allowedProcesses;

  /**
   * Die Metadaten des Projektes dürfen gelesen werden. Es dürfen Kommentare geschrieben werden. Es dürfen Attachments
   * bearbeitet werden (read/write/delete)
   */
  @Column(name = "READRIGHTS")
  String readRights;

  /**
   * erlaubt die Bearbeitung eines Projektes in jeder Phase. Es können Metadaten verändert werden. Es können Prozesse
   * angelegt werden. Es können Prozess gestartet werden.
   */
  @Column(name = "WRITERIGHTS")
  String writeRights;

  /**
   * Mit dieser Berechtigung ist es möglich das Projekt anzulegen. Die Daten des Projektes können bearbeiten werden
   * solange der Projektstatus "NEW" ist. Wenn auf dem Projekttyp direkt (keine Phase vorhanden) die erlaubten Prozesse
   * definiert sind dann können diese angelegt aber nicht gestartet werden.
   */
  @Column(name = "CREATERIGHTS")
  String createRights;

  /**
   * 
   */
  @Column(name = "DELETERIGHTS")
  String deleteRights;

  /**
   * Hier ist die Prozessdefinition eingetragen welche beim anlegen des Projektes
   */
  @Column(name = "STARTPROCESS")
  String startProcess;

  @ManyToMany
  @JoinTable(name = "PMC_PROJECTTYPE_MILESTONE", joinColumns = @JoinColumn(name = "PROJECTTYPE", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "MILESTONE", referencedColumnName = "GUID"))
  Collection<Milestone> allowedMilestones;

  @ManyToMany
  @JoinTable(name = "PMC_PROJECTTYPE_CARDLIST", joinColumns = @JoinColumn(name = "REF_PROJECTTYPE", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "REF_CARDLIST", referencedColumnName = "GUID"))
  Collection<CardList> cardLists;

  /**
   * Definiert den Typ des RefObjects; auf diesem Typ basiert die Auswahl des RefObjects beim Anlegen des Projektes.
   * Bsp: "SI", "CHAIN", "LI", ...
   */
  @Column(name = "OBJECTTYPE")
  String objectType;

  /**
   * Hier kann eine Liste von Gruppen angegeben werden (comma seperated groupnames); für jede dieser Gruppen wird dann
   * im Project Header und beim Erstellen des Projektes ein Dropdown eingeblendet über welches ein User aus dieser
   * Gruppe ausgewählt werden kann. Die Auswahl wird als Project Property persistiert
   */
  @Column(name = "ROLES")
  String roles;

  /**
   * Liste (comma seperated strings) der Subtypen; sind hier Werte vorhanden, stehen beim Erstellen des Projekts, bei
   * der Auswahl des Projekttypen, ebenfalls alle Subtypen zur Asuwahl. Die Auswahl des Subtypen wird direkt in der
   * PMC_PROJECT Tabelle persistiert.
   */
  @Column(name = "ALLOWED_SUBTYPES")
  String allowedSubtypes;

  /**
   * Hier können Project Properties definiert werden, welche im Projektheader angezeigt werden. Konfigurierbar sind
   * Anzeigename, Propertyname, Typ (dropdown, checkbox), default Werte, auswählbare Werte und Position im Header.
   */
  @Column(name = "ADDITIONAL_PROPERTIES")
  String additionalProperties;

  /**
   * Feld für diverse Konfigurationen (JSON)
   * <ul>
   * <li>technische Details zum starten von Subprozessen</li>
   * <li>typspezifische Projekteinfoseite</li>
   * <li>"inform on cancellation" User</li>
   * <li>Projekteigenschaften von Parent als default übernehmen</li>
   * <li>Rollen im UI verbergen</li>
   * <li>tag selection freischalten</li>
   * <li>multiple Tags erlaubt</li>
   * <li>Properties für Subprozesse</li>
   * </ul>
   */
  @Column(name = "CONFIGURATION")
  String configuration;

  /**
   * Autorization- und Projekteventlistener; Projekteventlistener stellen Event bei Projektstart, -stop, -cancellation,
   * etc. zur Verfügung.
   */
  @Column(name = "LISTENERS")
  String listeners;

  @OneToMany(mappedBy = "pmcProjectType")
  Collection<PmcProjectPhaseDefinition> phases;

  @Column(name = "DETAILS_TEMPLATE")
  String detailsTemplate;

  @Column(name = "PROPERTIES_TEMPLATE")
  String propertiesTemplate;

  @Column(name = "CREATION_PROPERTIES_TEMPLATE")
  String creationPropertiesTemplate;

  public PmcProjectType() {
    super();
  }

  public List<String> roles() {
    if (StringUtils.isEmpty(this.roles))
      return Collections.emptyList();
    return Arrays.asList(StringUtils.stripAll(roles.split(",")));
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection<PmcProjectType> getAllowedChildTypes() {
    return allowedChildTypes;
  }

  public void setAllowedChildTypes(Collection<PmcProjectType> allowedChildTypes) {
    this.allowedChildTypes = allowedChildTypes;
  }

  /**
   * @return the {@link PmcProjectType#allowedTags} of the project
   * @see PmcProject#tag
   */
  public String getAllowedTags() {
    return allowedTags;
  }

  public void setAllowedTags(String allowedTags) {
    this.allowedTags = allowedTags;
  }

  public String getAllowedProcesses() {
    return allowedProcesses;
  }

  public void setAllowedProcesses(String allowedProcesses) {
    this.allowedProcesses = allowedProcesses;
  }

  public List<DocuConfig> getDocuConfigs() {
    return docuConfigs;
  }

  public void setDocuConfigs(List<DocuConfig> docuConfigs) {
    this.docuConfigs = docuConfigs;
  }

  public String getReadRights() {
    return readRights;
  }

  public void setReadRights(String readRights) {
    this.readRights = readRights;
  }

  public String getWriteRights() {
    return writeRights;
  }

  public void setWriteRights(String writeRights) {
    this.writeRights = writeRights;
  }

  public String getDeleteRights() {
    return deleteRights;
  }

  public void setDeleteRights(String deleteRights) {
    this.deleteRights = deleteRights;
  }

  public Collection<Milestone> getAllowedMilestones() {
    return allowedMilestones;
  }

  public void setAllowedMilestones(Collection<Milestone> allowedMilestones) {
    this.allowedMilestones = allowedMilestones;
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public String getStartProcess() {
    return startProcess;
  }

  public void setStartProcess(String startProcess) {
    this.startProcess = startProcess;
  }

  public String getDetailsTemplate() {
    return detailsTemplate;
  }

  public void setDetailsTemplate(String detailsTemplate) {
    this.detailsTemplate = detailsTemplate;
  }

  public String getPropertiesTemplate() {
    return propertiesTemplate;
  }

  public void setPropertiesTemplate(String propertiesTemplate) {
    this.propertiesTemplate = propertiesTemplate;
  }

  public String getCreationPropertiesTemplate() {
    return creationPropertiesTemplate;
  }

  public void setCreationPropertiesTemplate(String creationPropertiesTemplate) {
    this.creationPropertiesTemplate = creationPropertiesTemplate;
  }

  @Override
  public String toString() {
    return "PmcProjectType [name=" +
      name +
      ", idName=" +
      idName +
      ", docuConfigs=" +
      docuConfigs +
      ", allowedTags=" +
      allowedTags +
      ", allowedProcesses=" +
      allowedProcesses +
      ", readRights=" +
      readRights +
      ", writeRights=" +
      writeRights +
      ", deleteRights=" +
      deleteRights +
      ", deleteRights=" +
      deleteRights +
      ", startProcess=" +
      startProcess +
      ", allowedMilestones=" +
      allowedMilestones +
      ", objectType=" +
      objectType +
      ", roles=" +
      roles +
      ", allowedSubtypes=" +
      allowedSubtypes +
      ", additionalProperties=" +
      additionalProperties +
      ", configuration=" +
      configuration +
      ", detailsTemplate=" +
      detailsTemplate +
      ", propertiesTemplate=" +
      propertiesTemplate +
      ", creationPropertiesTemplate=" +
      creationPropertiesTemplate +
      "]";
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }

  public String getAllowedSubtypes() {
    return allowedSubtypes;
  }

  public void setAllowedSubtypes(String allowedSubtypes) {
    this.allowedSubtypes = allowedSubtypes;
  }

  public String getAdditionalProperties() {
    return additionalProperties;
  }

  public void setAdditionalProperties(String additionalProperties) {
    this.additionalProperties = additionalProperties;
  }

  public String getConfiguration() {
    return configuration;
  }

  public void setConfiguration(String configuration) {
    this.configuration = configuration;
  }

  public Collection<String> getListeners() {
    if (listeners != null)
      return new ArrayList<>(Arrays.asList(StringUtils.stripAll(listeners.split(","))));
    else
      return Collections.emptyList();
  }

  public void setListeners(Collection<String> listeners) {
    String result = "";
    for (String s : listeners)
      result += s + ", ";
    if (listeners.size() > 0)
      result = result.substring(0, result.length() - 2);
    this.listeners = result;
  }

  public String getCreateRights() {
    return createRights;
  }

  public void setCreateRights(String createRights) {
    this.createRights = createRights;
  }

  public Collection<PmcProjectPhaseDefinition> getPhases() {
    return phases;
  }

  public void setPhases(Collection<PmcProjectPhaseDefinition> phases) {
    this.phases = phases;
  }

  public Collection<CardList> getCardLists() {
    return cardLists;
  }

  public void setCardLists(Collection<CardList> cardLists) {
    this.cardLists = cardLists;
  }

}
