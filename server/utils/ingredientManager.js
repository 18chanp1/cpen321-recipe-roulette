const { Models } = require("../utils/db");

removeIngredients = async (userId, ingredientList) => {
  let userIngredient = await Models.Ingredient.findOne({ userId });
  if (userIngredient == null) {
    return;
  }
  let newIngredients = [];
  userIngredient.ingredients.forEach(ingredient => {
    if (!ingredientList.includes(ingredient.name)) {
      newIngredients.push({
        name: ingredient.name,
        count: ingredient.count,
        date: ingredient.date
      })
    }
  });
  userIngredient.ingredients = newIngredients;
  await userIngredient.save();
}

module.exports = removeIngredients;