var express = require('express');
const { locals } = require('../app');
var router = express.Router();
var dbFunctions = require('../../db/db').Functions;
var dbModels = require('../../db/db').Models;

router.get('/', async function(req, res, next) {
  let userId = req.headers.email;
  if (!userId) {
    res.status(400);
    res.send("Missing user email");
    return;
  }
  let recipes = await dbFunctions.dbFindAllRecords(dbModels.Recipe, {userId});
  let flavorProfile = "No Flavor Profile available";
  if (recipes.length == 0) {
    res.status(200);
    res.send(flavorProfile);
    return;
  }
  let meat_indicators = [
    "stuff",
    "bone",
    "blood",
    "fowl",
    "game",
    "meat",
    "halal",
    "stew",
    "pepperoni",
    "bird",
    "veal",
    "veau",
    "escargot",
    "flesh",
    "cut",
    "jerk",
    "jerky",
    "lamb",
    "cold cuts",
    "mouton",
    "mutton",
    "organs",
    "pemican",
    "pemmican",
    "porc",
    "pork",
    "pancetta",
    "guanciale",
    "boeuf",
    "sausage",
    "sausage meat",
    "snail",
    "beef",
    "chicken",
    "duck",
    "pate"
  ];

  let cheese_indicators = [
    "cheese",
    "brie",
    "camembert",
    "cheddar",
    "chevre",
    "edam",
    "bleu",
    "parmesan",
    "quark",
    "ricotta",
    "velveeta",
    "gouda",
    "liederkranz",
    "limburger",
    "mozzarella",
    "muenster"
  ];

  let pastry_indicators = [
    "pate feuillete",
    "frangipane",
    "pastry",
    "pandowdy",
    "patty shell",
    "pie",
    "pie crust",
    "pie shell",
    "profiterole",
    "puff",
    "rugelach",
    "ruggelach",
    "rugulah",
    "sausage roll",
    "streusel",
    "strudel",
    "tart",
    "timbale",
    "timbale case",
    "toad-in-the-hole",
    "vol-au-vent",
    "baklava",
    "bouchee",
    "dowdy",
    "baked"
  ];

  // [Meat, Cheese, Pastry, Veggie]
  let meat = 0;
  let cheese = 1;
  let pastry = 2;
  let globalScore = [0, 0, 0, 0];
  const flavorProfiles = [
    [
      "Carnivore Crusader",
      "Protein Paladin",
      "Savory Samurai"
    ],
    [
      "Cheese Connoisseur",
      "Dairy Dandy",
      "Fromage Fanatic",
    ],
    [
      "Diva Dough",
      "Sweet Swirler",
      "Bakery Buff"
    ],
    [
      "Green Gourmet",
      "Veggie Virtuoso",
      "Plant-based Picasso"
    ]
  ]
  recipes.forEach(recipe => {
    let localScore = [0, 0, 0, recipe.numTimes];
    for (let i = 0; i < pastry_indicators.length; i++) {
      if (recipe.recipeName.toLowerCase().includes(pastry_indicators[i])) {
        localScore[pastry] += recipe.numTimes;
      }
    }
    for (let i = 0; i < cheese_indicators.length; i++) {
      if (recipe.recipeName.toLowerCase().includes(cheese_indicators[i])) {
        localScore[cheese] += recipe.numTimes;
        break;
      }
    }
    for (let i = 0; i < meat_indicators.length; i++) {
      if (recipe.recipeName.toLowerCase().includes(meat_indicators[i])) {
        localScore[meat] += recipe.numTimes;
        break;
      }
    }
    let winner = localScore.indexOf(Math.max(...localScore));
    globalScore[winner] += recipe.numTimes;
  });

  let winner = globalScore.indexOf(Math.max(...globalScore));
  res.status(200);
  res.send(flavorProfiles[winner][Math.floor(Math.random() * 3)]);
});

module.exports = router;
