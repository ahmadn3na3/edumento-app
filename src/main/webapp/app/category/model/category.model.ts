/**
 * {
	  "name": "Algebra",
	  "nameAr": "الجبر",
	  "color": null,
	  "image": "//content.mintplatform.net/img/default/image/Math.png",
	  "thumbnail": "//content.mintplatform.net/img/default/thumbnail/Math_thum.png",
	  "organizationId": null,
	  "foundationId": null,
	  "parentId": null,
	  "chapters": [],
	  "grades": [],
	  "id": 13,
	  "organization": null,
	  "foundation": null,
	  "parentCategory": null
	}
 * 
 * 
 */

export class Category {
    name: string;
    nameAr?: string;
    color?: string;
    image?: string;
    thumbnail?: string;
    organizationId?: number;
    foundationId?: number;
    parentId?: number;
    chapters?: any[];
    grades?: any[];
    id?: number;
    organization?: any;
    foundation?: any;
    parentCategory?: any;

    constructor({
        name,
        nameAr,
        id,
        color,
        image,
        thumbnail,
        organizationId,
        foundationId,
        parentId,
        chapters,
        grades,
    }: any) {
        this.name = name;
        this.nameAr = nameAr;
        this.color = color;
        this.image = image;
        this.thumbnail = thumbnail;
        this.organizationId = organizationId;
        this.foundationId = foundationId;
        this.parentId = parentId;
        this.chapters = chapters;
        this.grades = grades;
        this.id = id;
    }
}
