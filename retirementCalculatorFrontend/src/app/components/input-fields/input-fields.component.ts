import { Component, OnInit,Input, Output, EventEmitter, SimpleChanges  } from '@angular/core';
import { CommonModule} from '@angular/common';
import {FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule} from '@angular/forms';
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

  formFields = ['currentAge', 'retirementAge', 'interestRate', 'lifestyleType'];

  lifestyleOptions = [
    { value: 'simple', viewValue: 'Simple' },
    { value: 'fancy', viewValue: 'Fancy' }
  ];

  retirementForm!: FormGroup;

  ngOnInit(): void {
    const group: any = {};

    this.formFields.forEach(field => {
      if (field === 'lifestyleType') {
        group[field] = new FormControl('', Validators.required);
      } else {
        group[field] = new FormControl('', [
          Validators.required,
          Validators.min(1)
        ]);
      }
    });

    this.retirementForm = new FormGroup(group);

    this.retirementForm.valueChanges.subscribe(val => {
          this.formValuesChanged.emit(val);
        });
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
