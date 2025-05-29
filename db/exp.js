const cds = require('@sap/cds');
const fs = require('fs');
const path = require('path');

// Path to your schema file
const filePath = path.join(__dirname, './schema.cds');

// Read file content
const fileContent = fs.readFileSync(filePath, 'utf8');

// Parse cds file
let csn = cds.parse.cdl(fileContent);
let json = cds.compile.to.json(csn);
console.log(json);