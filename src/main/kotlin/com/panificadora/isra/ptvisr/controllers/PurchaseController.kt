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
        return "layout :: mainPage(page='purchase_new', fragment='content')"
    }

    @PostMapping("/new")
    fun savePurchase(@ModelAttribute purchaseFormDto: PurchaseFormDto, model: Model): String {
        // Set purchaseDate to current LocalDateTime in the backend
        purchaseFormDto.purchaseDate = LocalDateTime.now()
        return try {
            purchaseService.savePurchase(purchaseFormDto)
            model.addAttribute("successMessage", "Compra registrada exitosamente!");
            "layout :: mainPage(page='purchase_new', fragment='content')" // Redirect back to the new purchase page or a list
        } catch (e: IllegalArgumentException) {
            model.addAttribute("errorMessage", "Error al registrar compra: ${e.message}");
            "layout :: mainPage(page='purchase_new', fragment='content')" // Redirect back to the form with error
        } catch (e: Exception) {
            model.addAttribute("errorMessage", "Ocurrió un error inesperado al registrar la compra. ${e.message}");
            "layout :: mainPage(page='purchase_new', fragment='content')"
        }
    }
}
