import { Component, Input, Output, EventEmitter } from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import { CommonModule} from '@angular/common';

@Component({
  selector: 'app-submit-button',
  imports: [MatButtonModule, CommonModule],
  templateUrl: './submit-button.component.html',
  styleUrl: './submit-button.component.scss'
})
export class SubmitButtonComponent {

@Input() formData: any;
  @Output() submitClicked = new EventEmitter<any>();
    @Output() clear = new EventEmitter<void>();
showResult: boolean = false;


  submit() {
    this.submitClicked.emit(this.formData);
      this.showResult = true;

  }

clearFields(){
this.clear.emit(this.formData);
  this.showResult = false;

  }
}
