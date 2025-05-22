import { Component, OnInit,Input, Output, EventEmitter, SimpleChanges  } from '@angular/core';
import { CommonModule} from '@angular/common';
import {FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';

@Component({
  selector: 'app-input-fields',
  imports: [CommonModule, FormsModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule,MatSelectModule],
  templateUrl: './input-fields.component.html',
  styleUrl: './input-fields.component.scss'
})
export class InputFieldsComponent implements OnInit{

  @Output() formValuesChanged = new EventEmitter<any>();
  @Input() resetFormTrigger: boolean = false;
@Output() formValid = new EventEmitter<boolean>();

  formFields = ['currentAge', 'retirementAge', 'interestRate', 'lifestyleType'];

  lifestyleOptions = [
    { value: 'simple', viewValue: 'Simple' },
    { value: 'fancy', viewValue: 'Fancy' }
  ];

  retirementForm!: FormGroup;


    ngOnInit() {
      this.retirementForm = new FormGroup({
        currentAge: new FormControl('', [
          Validators.required,
          Validators.min(17),
          Validators.max(120),
          Validators.pattern(/^\d+$/)
        ]),
        retirementAge: new FormControl('', [
          Validators.required,
          Validators.min(17),
          Validators.max(100),
          Validators.pattern(/^\d+$/),
          this.retirementAgeValidator()
        ]),
        interestRate: new FormControl('', [
          Validators.min(0),
          Validators.max(100),
          Validators.pattern(/^\d+(\.\d{1,2})?$/)
        ]),
        lifestyleType: new FormControl('', Validators.required)
      });

      // Re-validate retirementAge when currentAge changes
      this.retirementForm.get('currentAge')?.valueChanges.subscribe(() => {
        this.retirementForm.get('retirementAge')?.updateValueAndValidity();
      });

      this.retirementForm.valueChanges.subscribe(val => {
        this.formValuesChanged.emit(val);
        console.log("val", val);
        if(val != null){
        this.formValid.emit(this.retirementForm.valid);
        console.log(this.retirementForm.valid)
        }
      });
    }



retirementAgeValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const retirementAge = control.value;
    const formGroup = control.parent;

    if (!formGroup) return null;

    const currentAge = formGroup.get('currentAge')?.value;

    if (retirementAge && currentAge && retirementAge <= currentAge) {
      return { retirementAgeInvalid: true };
    }

    return null;
  };
}

ngOnChanges(changes: SimpleChanges) {
  if (
    changes['resetFormTrigger'] &&
    !changes['resetFormTrigger'].firstChange &&
    this.retirementForm
  ) {
    this.retirementForm.reset();
  }
}


  toLabel(field: string): string {
    return field
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, str => str.toUpperCase());
  }

}
