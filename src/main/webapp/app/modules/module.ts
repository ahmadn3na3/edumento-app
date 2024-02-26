export class Module {
	
	constructor(name: string, description: string, key: string, id: number) {
		this.name = name;
		this.description = description;
		this.key = key;
		this.id = id;
	}

	name: string | undefined;
	description: string | undefined;
	key: string | undefined;
	id: number = 0;
	permissions!: Permission[];
}
/*{
	"id": 1,
		"name": "permission1",
			"keycode": "mint.space",
				"code": 0,
					"type": "ADMIN"
}*/
export class Permission {

	constructor(id: number, name: string, keycode: string, code: number, type: string) {
		this.id = id;
		this.name = name;
		this.keycode = keycode;
		this.code = code;
		this.type = type;
	}

	id: number = 0;
	name: string | undefined;
	keycode: string | undefined;
	code: number = 0;
	type: string | undefined;
}
