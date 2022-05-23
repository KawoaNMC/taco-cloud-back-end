package tacos.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import tacos.Order;
import tacos.Taco;
import tacos.data.OrderRepository;
import tacos.data.TacoRepository;
import tacos.dto.OrderDto;
import tacos.dto.TacoDto;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {
	private final OrderRepository orderRepo;
	private final TacoRepository tacoRepo;

	@Autowired
	public OrderController(OrderRepository orderRepo, TacoRepository tacoRepo) {
		this.orderRepo = orderRepo;
		this.tacoRepo = tacoRepo;
	}

	@GetMapping("/current")
	public String orderForm(Model model) {
		model.addAttribute("order", new Order());
		return "orderForm";
	}

	@PostMapping
	public String processOrder(@RequestBody OrderDto orderDto, Errors errors) {
		
		List<Taco> tacos = new ArrayList<>();
		for(Long i : orderDto.getTacoIds()) {
			Optional<Taco> t = tacoRepo.findById(i);
			
			if(t.isPresent()) {
				tacos.add(t.get());
			}
		}
		
		Order o = new Order();
		o.setCcCVV(orderDto.getCcCVV());
		o.setCcExpiration(orderDto.getCcExpiration());
		o.setCity(orderDto.getCity());
		o.setName(orderDto.getName());
		o.setState(orderDto.getState());
		o.setPlacedAt(new Date());
		o.setStreet(orderDto.getStreet());
		o.setZip(orderDto.getZip());
		o.setTacos(tacos);
		orderRepo.save(o);
		return "redirect:/";
	}

}
