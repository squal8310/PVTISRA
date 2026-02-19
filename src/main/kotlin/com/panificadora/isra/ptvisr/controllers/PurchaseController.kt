package com.panificadora.isra.ptvisr.controllers

import com.panificadora.isra.ptvisr.dtos.PurchaseFormDto
import com.panificadora.isra.ptvisr.repositories.CategoryRepository
import com.panificadora.isra.ptvisr.repositories.SupplierRepository
import com.panificadora.isra.ptvisr.repositories.UnitOfMeasureRepository
import com.panificadora.isra.ptvisr.services.PurchaseService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDateTime


@Controller
@RequestMapping("/purchases")
class PurchaseController(
    private val supplierRepository: SupplierRepository,
    private val categoryRepository: CategoryRepository,
    private val unitOfMeasureRepository: UnitOfMeasureRepository,
    private val purchaseService: PurchaseService // Inject PurchaseService
) {

    @GetMapping("/new")
    fun showNewPurchasePage(model: Model): String {
        model.addAttribute("suppliers", supplierRepository.findAll())
        model.addAttribute("categories", categoryRepository.findAll())
        model.addAttribute("unitsOfMeasure", unitOfMeasureRepository.findAll())
        model.addAttribute("purchaseFormDto", PurchaseFormDto(purchaseDate = LocalDateTime.now())) // Add DTO for form binding
        return "purchase_new"
    }

    @PostMapping("/new")
    fun savePurchase(@ModelAttribute purchaseFormDto: PurchaseFormDto, redirectAttributes: RedirectAttributes): String {
        // Set purchaseDate to current LocalDateTime in the backend
        purchaseFormDto.purchaseDate = LocalDateTime.now()
        return try {
            purchaseService.savePurchase(purchaseFormDto)
            redirectAttributes.addFlashAttribute("successMessage", "Compra registrada exitosamente!")
            "redirect:/purchases/new" // Redirect back to the new purchase page or a list
        } catch (e: IllegalArgumentException) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar compra: ${e.message}")
            "redirect:/purchases/new" // Redirect back to the form with error
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado al registrar la compra.")
            "redirect:/purchases/new"
        }
    }
}
