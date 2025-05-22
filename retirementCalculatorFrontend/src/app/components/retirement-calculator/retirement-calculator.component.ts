import { Component,Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputFieldsComponent } from '../input-fields/input-fields.component';
import { SubmitButtonComponent } from '../submit-button/submit-button.component';
import { ResultsTableComponent } from '../results-table/results-table.component';
import { RetirementService } from '../../services/retirement.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-retirement-calculator',
  imports: [CommonModule,InputFieldsComponent, SubmitButtonComponent,ResultsTableComponent,MatFormFieldModule,MatInputModule,],
  templateUrl: './retirement-calculator.component.html',
  styleUrl: './retirement-calculator.component.scss'
})
export class RetirementCalculatorComponent {

  constructor(private retirementService: RetirementService) {}

formData: any = {};
calculatedFutureValue: any | null = null;
errorMessage: any | null = null;
showResult: boolean = false;
showError: boolean = false;
resetFormTrigger: boolean = false;
disableButton: boolean = false;
resultString: any | null = null;
tableData: any[] = [];
isFormValid: boolean = false;

onFormChange(data: any) {
  this.formData = data;
}


onFormValidChange(valid: boolean) {
  this.isFormValid = valid;
console.log(" is form valid",this.isFormValid)
}

onSubmit(data: any) {
  console.log('Final submitted data:', data);

  this.retirementService.calculateRetirement(data).subscribe({
    next: (result: any) => {
    console.log('Received data:', data);

      console.log('Received future value from backend:', result);

    this.tableData = [result]; // Or push to grow the list
    console.log('Emitted result:', result);

      this.showResult = true;
      this.showError = false;
    },
    error: (err: any) => {
      console.error('Error calculating retirement:', err);
      this.errorMessage = err.message;
            this.showError = true;

    }
  });
}




onClear() {
  this.formData = {};
  this.calculatedFutureValue = null;
  this.showResult = false;
  this.showError = false;

   this.resetFormTrigger = !this.resetFormTrigger;
this.isFormValid = false;
}


}
