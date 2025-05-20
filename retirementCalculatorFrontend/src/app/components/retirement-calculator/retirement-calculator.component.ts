import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputFieldsComponent } from '../input-fields/input-fields.component';
import { SubmitButtonComponent } from '../submit-button/submit-button.component';
import { RetirementService } from '../../services/retirement.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-retirement-calculator',
  imports: [CommonModule,InputFieldsComponent, SubmitButtonComponent,MatFormFieldModule,MatInputModule,],
  templateUrl: './retirement-calculator.component.html',
  styleUrl: './retirement-calculator.component.scss'
})
export class RetirementCalculatorComponent {

  constructor(private retirementService: RetirementService) {}

formData: any = {};
calculatedFutureValue: number | null = null;
showResult: boolean = false;
resetFormTrigger: boolean = false;

onFormChange(data: any) {
  this.formData = data;
}

onSubmit(data: any) {
  console.log('Final submitted data:', data);

  this.retirementService.calculateRetirement(data).subscribe({
    next: (result: any) => {
      console.log('Received future value from backend:', result);
      this.calculatedFutureValue = result.futureValue;
      this.showResult = true;
    },
    error: (err: any) => {
      console.error('Error calculating retirement:', err);
    }
  });
}

onClear() {
  this.formData = {};
  this.calculatedFutureValue = null;
  this.showResult = false;

    this.resetFormTrigger = !this.resetFormTrigger;

}

}
