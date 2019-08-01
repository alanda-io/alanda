
import { DmsDocument } from "./DmsDocument";
import { Tag } from './tag';

const tag0 = new Tag();
tag0.identifier = '/tags/photo/aufstellungsort';
tag0.tagName = 'aufstellungsort';

const tag1 = new Tag();
tag1.identifier = '/tags/bau/umbau';
tag1.tagName = 'umbau';


const tag2 = new Tag();
tag2.identifier = '/tags/bau/neubau';
tag2.tagName = 'neubau';

export const MOCK_DOCUMENTS: DmsDocument[] = [
  { guid: '0', name: '560035A_20171207_RX-PB-Konfig.pdf', authorName: 'Augustus Weigel', lastModified: new Date(2017, 2, 4), size: 188,  versionString: '1', tags: [tag0, tag1, tag2] },
  { guid: '1', name: '560035A_20170226_RX-PB-Konfig.pdf', authorName: 'Augustus Weigel', lastModified: new Date(2014, 5, 6), size: 200, versionString: '4', tags: [tag0, tag2] },
  { guid: '2', name: '540121A_Baufertigstellungsmeldung_01von01.pdf', authorName: 'Augustus Weigel', lastModified: new Date(2014, 5, 6), size: 64,  versionString: '12', tags: [tag0, tag2] },
  { guid: '3', name: '540121A_Gesamtansicht_Klimaaussengeraet_01von01.JPG', authorName: 'Augustus Weigel', lastModified: new Date(2014, 5, 6), size: 120,  versionString: '1', tags: [tag0, tag2] },
  { guid: '4', name: '540121A_Kondensatleitung_01von10.JPG', authorName: 'Drew McKellar', lastModified: new Date(2014, 5, 6), size: 4200,  versionString: '1', tags: [tag0, tag2] },
  { guid: '5', name: '540121A_Kondensatleitung_02von10.JPG', authorName: 'Drew McKellar', lastModified: new Date(2014, 5, 6), size: 1600,  versionString: '1', tags: [tag0, tag2] },
  { guid: '6', name: '540121A_Kondensatleitung_03von10.JPG', authorName: 'Drew McKellar', lastModified: new Date(2013, 5, 6), size: 1300,  versionString: '1', tags: [tag0, tag2] },
  { guid: '7', name: '540121A_Kondensatleitung_04von10.JPG', authorName: 'Drew McKellar', lastModified: new Date(2012, 5, 6), size: 1100,  versionString: '1', tags: [tag0, tag2] },
  { guid: '8', name: '540121A_Kondensatleitung_05von10.JPG', authorName: 'Drew McKellar', lastModified: new Date(2011, 5, 6), size: 1300,  versionString: '1', tags: [tag0, tag2] },
  { guid: '9', name: '540121A_Kondensatleitung_06von10.JPG', authorName: 'Drew McKellar', lastModified: new Date(2010, 5, 6), size: 1500,  versionString: '1', tags: [tag1] },
  { guid: '10', name: '540121A_Kondensatleitung_07von10.JPG', authorName: 'Fritz Dirksen', lastModified: new Date(2010, 5, 6), size: 1700,  versionString: '1', tags: [tag1] },
  { guid: '11', name: '540121A_Kondensatleitung_08von10.JPG', authorName: 'Fritz Dirksen', lastModified: new Date(2011, 5, 6), size: 1900,  versionString: '1', tags: [tag1] },
  { guid: '12', name: '540121A_Kondensatleitung_09von10.JPG', authorName: 'Fritz Dirksen', lastModified: new Date(2012, 5, 6), size: 2800,  versionString: '1', tags: [tag1] },
  { guid: '13', name: '540121A_Kondensatleitung_10von10.JPG', authorName: 'Fritz Dirksen', lastModified: new Date(2013, 5, 6), size: 4000,  versionString: '1', tags: [tag1] }
]

export const MOCK_VERSIONS: DmsDocument[] = [
  { guid: '1', name: '540121A_Gesamtansicht_Klimaaussengeraet', authorName: 'Augustus Weigel', lastModified: new Date(2010, 5, 6), size: 64,  versionString: '1', tags: [tag0, tag2] },
  { guid: '2', name: '540121A_Gesamtansicht_Klimaaussengeraet', authorName: 'Drew McKellar', lastModified: new Date(2011, 5, 6), size: 55,  versionString: '2', tags: [tag2, tag0] },
  { guid: '3', name: '540121A_Gesamtansicht_Klimaaussengeraet', authorName: 'Augustus Weigel', lastModified: new Date(2017, 5, 6), size: 68,  versionString: '3', tags: [tag2, tag0] }
]

export const MOCK_TAGS: Tag[] = [tag0, tag1, tag2]
