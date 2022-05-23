package tacos.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.extern.slf4j.Slf4j;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;
import tacos.dto.TacoDto;
import tacos.Ingredient;
import tacos.Ingredient.Type;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
	private final IngredientRepository ingredientRepo;
	private final TacoRepository tacoRepo;

	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepo) {
		this.ingredientRepo = ingredientRepo;
		this.tacoRepo = tacoRepo;
	}

	@ModelAttribute
	public void addIngredientsToModel(Model model) {
		List<Ingredient> ingredients =
				(List<Ingredient>)ingredientRepo.findAll();
		Type[] types = Ingredient.Type.values();
		for (Type type : types) {

			model.addAttribute(type.toString().toLowerCase(),

					filterByType(ingredients, type));
		}
	}

	@GetMapping
	public String showDesignForm(Model model) {
		model.addAttribute("taco", new Taco());
		return "design";
	}

	@PostMapping
	@ResponseBody
	public ResponseEntity<Long> processDesign(@RequestBody TacoDto tacoDto, Errors errors) {
	
		Taco taco = new Taco();
		taco.setName(tacoDto.getName());
		List<Ingredient> is = new ArrayList<>();
		log.info("Processing design: " + taco);
		for(String iStr : tacoDto.getIngredients()) {
			Optional<Ingredient> i = ingredientRepo.findById(iStr);
			if(i.isPresent()) {
				is.add(i.get());
			}
		}
		taco.setIngredients(is);
		taco = tacoRepo.save(taco);
		return ResponseEntity.ok(taco.getId());
	}

	private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
		List<Ingredient> ingrList = new ArrayList<Ingredient>();
		for (Ingredient ingredient : ingredients) {
			if (ingredient.getType().equals(type))

				ingrList.add(ingredient);
		}
		return ingrList;
	}
}
